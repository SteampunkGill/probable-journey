<template>
  <div class="product-manage">
    <div class="action-bar card">
      <div class="search-form">
        <input v-model="query.name" placeholder="商品名称" class="admin-input" />
        <select v-model="query.categoryId" class="admin-select">
          <option value="">全部分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <button class="btn-primary" @click="loadProducts">查询</button>
      </div>
      <div class="batch-actions">
        <button class="btn-success" @click="showEditModal()">新增商品</button>
        <button class="btn-warning" :disabled="!selectedIds.length" @click="batchStatus(1)">批量上架</button>
        <button class="btn-danger" :disabled="!selectedIds.length" @click="batchStatus(0)">批量下架</button>
      </div>
    </div>

    <div class="table-container card">
      <table class="admin-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="toggleAll" :checked="isAllSelected" /></th>
            <th>图片</th>
            <th>名称</th>
            <th>分类</th>
            <th>价格</th>
            <th>成本</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id">
            <td><input type="checkbox" v-model="selectedIds" :value="p.id" /></td>
            <td><img :src="p.imageUrl" class="table-img" /></td>
            <td>{{ p.name }}</td>
            <td>{{ p.categoryName }}</td>
            <td>¥{{ p.price }}</td>
            <td>¥{{ p.costPrice || 0 }}</td>
            <td :class="{ 'text-danger': p.stock <= p.alertThreshold }">
              {{ p.stock }}
              <span v-if="p.stock <= p.alertThreshold" class="badge-warn">预警</span>
            </td>
            <td>{{ p.sales }}</td>
            <td>
              <span :class="p.status === 1 ? 'status-on' : 'status-off'">
                {{ p.status === 1 ? '已上架' : '已下架' }}
              </span>
            </td>
            <td class="ops">
              <button @click="showEditModal(p)">编辑</button>
              <button @click="showRecipeModal(p)">配方</button>
              <button @click="toggleStatus(p)">{{ p.status === 1 ? '下架' : '上架' }}</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- 编辑弹窗 -->
    <div v-if="editModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>{{ editModal.isEdit ? '编辑商品' : '新增商品' }}</h3>
        <form @submit.prevent="saveProduct" class="admin-form">
          <div class="form-item">
            <label>商品名称</label>
            <input v-model="editModal.form.name" required class="admin-input" />
          </div>
          <div class="form-item">
            <label>商品分类</label>
            <select v-model="editModal.form.categoryId" required class="admin-select">
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="form-item">
            <label>价格</label>
            <input v-model.number="editModal.form.price" type="number" step="0.01" required class="admin-input" />
          </div>
          <div class="form-item">
            <label>成本价</label>
            <input v-model.number="editModal.form.costPrice" type="number" step="0.01" class="admin-input" />
          </div>
          <div class="form-item">
            <label>库存</label>
            <input v-model.number="editModal.form.stock" type="number" required class="admin-input" />
          </div>
          <div class="form-item">
            <label>库存预警值</label>
            <input v-model.number="editModal.form.alertThreshold" type="number" class="admin-input" />
          </div>
          <div class="form-item">
            <label>商品图片</label>
            <div class="upload-box">
              <input type="file" @change="handleUpload" accept="image/*" />
              <img v-if="editModal.form.imageUrl" :src="editModal.form.imageUrl" class="preview-img" />
            </div>
          </div>
          <div class="form-item">
            <label>描述</label>
            <textarea v-model="editModal.form.description" class="admin-textarea"></textarea>
          </div>
          <div class="modal-footer">
            <button type="button" @click="editModal.show = false">取消</button>
            <button type="submit" class="btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>


    <!-- 配方管理弹窗 -->
    <div v-if="recipeModal.show" class="modal-mask">
      <div class="modal-container recipe-modal">
        <h3>配方管理 - {{ recipeModal.product.name }}</h3>
        <div class="recipe-list">
          <div v-for="(r, index) in recipeModal.list" :key="index" class="recipe-row">
            <select v-model="r.ingredientId" class="admin-select">
              <option v-for="i in ingredients" :key="i.id" :value="i.id">{{ i.name }} ({{ i.unit }})</option>
            </select>
            <input v-model="r.quantity" type="number" placeholder="用量" class="admin-input" />
            <button class="btn-text-danger" @click="removeRecipeRow(index)">删除</button>
          </div>
          <button class="btn-dashed" @click="addRecipeRow">+ 添加原料</button>
        </div>
        <div class="recipe-summary">
          预估成本：<span class="text-danger">¥{{ calculatedCost }}</span>
        </div>
        <div class="modal-footer">
          <button @click="recipeModal.show = false">取消</button>
          <button class="btn-primary" @click="saveRecipe">保存配方</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { get, post, put, uploadFile } from '../../../utils/request'

const products = ref([])
const categories = ref([])
const ingredients = ref([])
const selectedIds = ref([])
const query = ref({ name: '', categoryId: '' })

const recipeModal = ref({
  show: false,
  product: null,
  list: []
})
const editModal = ref({
  show: false,
  isEdit: false,
  form: {
    id: null,
    name: '',
    categoryId: '',
    price: 0,
    costPrice: 0,
    stock: 0,
    alertThreshold: 10,
    imageUrl: '',
    description: '',
    status: 1
  }
})


const isAllSelected = computed(() => products.value.length > 0 && selectedIds.value.length === products.value.length)

const calculatedCost = computed(() => {
  return recipeModal.value.list.reduce((total, r) => {
    const ing = ingredients.value.find(i => i.id === r.ingredientId)
    return total + (ing ? (ing.costPerUnit || 0) * (r.quantity || 0) : 0)
  }, 0).toFixed(2)
})

const loadProducts = async () => {
  const res = await get('/api/admin/products', query.value)
  products.value = res.data || []
}

const loadCategories = async () => {
  const res = await get('/api/admin/categories')
  // 后端 AdminCategoryController.getCategoryList 返回的是树形结构（只有 parent == null 的）
  // 我们需要将其平铺，或者在添加商品时只选择子分类
  const allCats = []
  const flatten = (list) => {
    list.forEach(c => {
      allCats.push(c)
      if (c.children && c.children.length) {
        flatten(c.children)
      }
    })
  }
  if (res.data) {
    flatten(res.data)
  }
  categories.value = allCats
}

const loadIngredients = async () => {
  const res = await get('/api/admin/inventory')
  ingredients.value = res.data || []
}

const toggleAll = (e) => {
  selectedIds.value = e.target.checked ? products.value.map(p => p.id) : []
}

const batchStatus = async (status) => {
  // 纯前端实现批量操作：循环发送单条请求
  const promises = selectedIds.value.map(id =>
    put(`/api/admin/products/${id}/status`, { status })
  )
  try {
    await Promise.all(promises)
    alert(`批量${status === 1 ? '上架' : '下架'}成功`)
    selectedIds.value = []
    loadProducts()
  } catch (error) {
    console.error('批量操作失败:', error)
    alert('部分商品操作失败，请重试')
    loadProducts()
  }
}

const toggleStatus = async (p) => {
  await put(`/api/admin/products/${p.id}/status`, { status: p.status === 1 ? 0 : 1 })
  loadProducts()
}

function showEditModal(p = null) {
  if (p) {
    editModal.value.isEdit = true
    editModal.value.form = { ...p }
  } else {
    editModal.value.isEdit = false
    editModal.value.form = {
      id: null,
      name: '',
      categoryId: '',
      price: 0,
      costPrice: 0,
      stock: 0,
      alertThreshold: 10,
      imageUrl: '',
      description: '',
      status: 1
    }
  }
  editModal.value.show = true
}

const handleUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  // 使用 uploadFile 函数，它会自动处理 FormData 和 headers
  try {
    const res = await uploadFile('/upload/image', file)
    // uploadFile 返回的是 FileUploadDTO 对象（因为拦截器提取了 data）
    editModal.value.form.imageUrl = res.url
  } catch (error) {
    console.error('上传失败:', error)
  }
}

const saveProduct = async () => {
  if (editModal.value.isEdit) {
    await put(`/api/admin/products/${editModal.value.form.id}`, editModal.value.form)
  } else {
    await post('/api/admin/products', editModal.value.form)
  }
  editModal.value.show = false
  loadProducts()
}

const showRecipeModal = async (p) => {
  recipeModal.value.product = p
  const res = await get(`/api/admin/recipes/product/${p.id}`)
  recipeModal.value.list = res.data.map(r => ({ ingredientId: r.ingredientId, quantity: r.quantity }))
  recipeModal.value.show = true
}

const addRecipeRow = () => {
  recipeModal.value.list.push({ ingredientId: '', quantity: 0 })
}

const removeRecipeRow = (index) => {
  recipeModal.value.list.splice(index, 1)
}

const saveRecipe = async () => {
  await post(`/api/admin/recipes/product/${recipeModal.value.product.id}`, recipeModal.value.list)
  recipeModal.value.show = false
  loadProducts()
}

onMounted(() => {
  loadProducts()
  loadCategories()
  loadIngredients()
})
</script>

<style scoped>
.product-manage { display: flex; flex-direction: column; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.action-bar { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; gap: 12px; }
.batch-actions { display: flex; gap: 12px; }

.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.table-img { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; }

.status-on { color: #52c41a; }
.status-off { color: #f5222d; }
.badge-warn { background: #fff7e6; color: #faad14; border: 1px solid #ffe7ba; padding: 2px 4px; border-radius: 4px; font-size: 12px; margin-left: 4px; }

.ops button { margin-right: 8px; color: #1890ff; background: none; border: none; cursor: pointer; }
.ops button:hover { text-decoration: underline; }

.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-container { background: white; padding: 24px; border-radius: 8px; width: 500px; }
.recipe-row { display: flex; gap: 12px; margin-bottom: 12px; }
.recipe-summary { margin-top: 20px; text-align: right; font-weight: bold; }
.modal-footer { margin-top: 24px; display: flex; justify-content: flex-end; gap: 12px; }

.admin-input { padding: 6px 12px; border: 1px solid #d9d9d9; border-radius: 4px; }
.admin-select { padding: 6px 12px; border: 1px solid #d9d9d9; border-radius: 4px; }
.btn-primary { background: #1890ff; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #52c41a; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-warning { background: #faad14; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-danger { background: #f5222d; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-dashed { border: 1px dashed #d9d9d9; background: white; width: 100%; padding: 8px; cursor: pointer; }
.btn-text-danger { color: #f5222d; background: none; border: none; cursor: pointer; }
.admin-form { display: flex; flex-direction: column; gap: 16px; }
.form-item { display: flex; flex-direction: column; gap: 8px; }
.form-item label { font-weight: bold; }
.admin-textarea { padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; min-height: 80px; }
.upload-box { display: flex; align-items: center; gap: 12px; }
.preview-img { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }

</style>