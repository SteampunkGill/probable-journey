<template>
  <div class="product-manage">
    <div class="action-bar card">
      <div class="search-form">
        <input v-model="filterQuery.name" placeholder="输入商品名称实时筛选..." class="admin-input" />
        <select v-model="filterQuery.categoryId" class="admin-select">
          <option value="">全部分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <button class="btn-primary" @click="loadProducts">同步数据</button>
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
          <tr v-for="p in filteredProducts" :key="p.id">
            <td><input type="checkbox" v-model="selectedIds" :value="p.id" /></td>
            <td><img :src="formatImageUrl(p.imageUrl)" class="table-img" /></td>
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
              <img v-if="editModal.form.imageUrl" :src="formatImageUrl(editModal.form.imageUrl)" class="preview-img" />
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
import { formatImageUrl } from '../../../utils/util'

// 原始数据状态
const products = ref([])
const categories = ref([])
const ingredients = ref([])
const selectedIds = ref([])

// 前端筛选条件
const filterQuery = ref({
  name: '',
  categoryId: ''
})

// 核心逻辑：纯前端实时筛选
const filteredProducts = computed(() => {
  const nameKeyword = filterQuery.value.name.trim().toLowerCase()
  const categoryId = filterQuery.value.categoryId

  return products.value.filter(p => {
    // 名称匹配逻辑
    const matchName = !nameKeyword || p.name.toLowerCase().includes(nameKeyword)
    // 分类匹配逻辑
    const matchCategory = !categoryId || String(p.categoryId) === String(categoryId)
    
    return matchName && matchCategory
  })
})

// 全选逻辑：仅针对筛选后的可见列表
const isAllSelected = computed(() => {
  return filteredProducts.value.length > 0 && 
         filteredProducts.value.every(p => selectedIds.value.includes(p.id))
})

const toggleAll = (e) => {
  if (e.target.checked) {
    // 将当前筛选出的所有商品ID加入选中列表（去重）
    const currentIds = filteredProducts.value.map(p => p.id)
    selectedIds.value = Array.from(new Set([...selectedIds.value, ...currentIds]))
  } else {
    // 从选中列表中移除当前筛选出的所有商品ID
    const currentIds = filteredProducts.value.map(p => p.id)
    selectedIds.value = selectedIds.value.filter(id => !currentIds.includes(id))
  }
}

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

const calculatedCost = computed(() => {
  return recipeModal.value.list.reduce((total, r) => {
    const ing = ingredients.value.find(i => i.id === r.ingredientId)
    return total + (ing ? (ing.costPerUnit || 0) * (r.quantity || 0) : 0)
  }, 0).toFixed(2)
})

// 加载数据：获取全部商品，不带筛选参数
const loadProducts = async () => {
  try {
    const res = await get('/api/admin/products')
    products.value = res.data || []
  } catch (error) {
    console.error('加载商品失败:', error)
  }
}

const loadCategories = async () => {
  const res = await get('/api/admin/categories')
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

const batchStatus = async (status) => {
  const promises = selectedIds.value.map(id =>
    put(`/api/admin/products/${id}/status`, { status })
  )
  try {
    await Promise.all(promises)
    alert(`批量${status === 1 ? '上架' : '下架'}成功`)
    selectedIds.value = []
    await loadProducts()
  } catch (error) {
    console.error('批量操作失败:', error)
    alert('部分商品操作失败，请重试')
    await loadProducts()
  }
}

const toggleStatus = async (p) => {
  try {
    await put(`/api/admin/products/${p.id}/status`, { status: p.status === 1 ? 0 : 1 })
    await loadProducts()
  } catch (error) {
    console.error('切换状态失败:', error)
  }
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
  try {
    const res = await uploadFile('/upload/image', file)
    // 后端返回的是 ApiResponse<FileUploadDTO>，数据在 data 字段中
    if (res.code === 200 || res.data) {
      editModal.value.form.imageUrl = res.data ? res.data.url : res.url
    }
  } catch (error) {
    console.error('上传失败:', error)
  }
}

const saveProduct = async () => {
  try {
    if (editModal.value.isEdit) {
      await put(`/api/admin/products/${editModal.value.form.id}`, editModal.value.form)
    } else {
      await post('/api/admin/products', editModal.value.form)
    }
    editModal.value.show = false
    await loadProducts()
  } catch (error) {
    console.error('保存商品失败:', error)
  }
}

const showRecipeModal = async (p) => {
  recipeModal.value.product = p
  try {
    const res = await get(`/api/admin/recipes/product/${p.id}`)
    recipeModal.value.list = res.data.map(r => ({ ingredientId: r.ingredientId, quantity: r.quantity }))
    recipeModal.value.show = true
  } catch (error) {
    console.error('加载配方失败:', error)
  }
}

const addRecipeRow = () => {
  recipeModal.value.list.push({ ingredientId: '', quantity: 0 })
}

const removeRecipeRow = (index) => {
  recipeModal.value.list.splice(index, 1)
}

const saveRecipe = async () => {
  try {
    await post(`/api/admin/recipes/product/${recipeModal.value.product.id}`, recipeModal.value.list)
    recipeModal.value.show = false
    await loadProducts()
  } catch (error) {
    console.error('保存配方失败:', error)
  }
}

onMounted(() => {
  loadProducts()
  loadCategories()
  loadIngredients()
})
</script>

<style scoped>
.product-manage {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  background: var(--background-color);
  min-height: 100vh;
  padding: var(--spacing-xl);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  color: var(--text-color-dark);
}

/* 卡片样式 */
.card {
  background: var(--surface-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
}

.card:hover {
  box-shadow: 0 12px 35px rgba(160, 82, 45, 0.12);
  transform: translateY(-2px);
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.search-form {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
}

.batch-actions {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

/* 表格容器 */
.table-container {
  overflow: hidden;
  border-radius: var(--border-radius-lg);
}

/* 表格样式 */
.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-family: 'Nunito', sans-serif;
}

.admin-table th,
.admin-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.25s ease-out;
}

.admin-table th {
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--primary-dark);
  font-weight: 600;
  background: rgba(255, 248, 220, 0.3);
  border-bottom: 2px solid var(--accent-brown);
  position: sticky;
  top: 0;
  z-index: 10;
}

.admin-table tbody tr {
  transition: all 0.25s ease-out;
}

.admin-table tbody tr:hover {
  background: rgba(255, 248, 220, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.05);
}

/* 商品图片 */
.table-img {
  width: 56px;
  height: 56px;
  border-radius: var(--border-radius-lg);
  object-fit: cover;
  border: 2px solid var(--accent-cream);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
  transition: all 0.3s ease-out;
}

.table-img:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

/* 状态标签 */
.status-on {
  color: #2c9678;
  background: rgba(44, 150, 120, 0.15);
  padding: 6px 16px;
  border-radius: var(--border-radius-xl);
  font-weight: 600;
  display: inline-block;
  font-family: 'Prompt', serif;
  border: 1px solid rgba(44, 150, 120, 0.3);
}

.status-off {
  color: #c13c3c;
  background: rgba(193, 60, 60, 0.15);
  padding: 6px 16px;
  border-radius: var(--border-radius-xl);
  font-weight: 600;
  display: inline-block;
  font-family: 'Prompt', serif;
  border: 1px solid rgba(193, 60, 60, 0.3);
}

/* 库存预警 */
.text-danger {
  color: #c13c3c;
  font-weight: 600;
}

.badge-warn {
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.2), rgba(255, 192, 203, 0.2));
  color: var(--accent-brown);
  border: 1px solid var(--accent-brown);
  padding: 4px 10px;
  border-radius: var(--border-radius-xl);
  font-size: 0.85em;
  font-weight: 500;
  margin-left: var(--spacing-sm);
  display: inline-block;
  font-family: 'Nunito', sans-serif;
}

/* 操作按钮 */
.ops {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.ops button {
  padding: 6px 16px;
  border: none;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  font-size: 0.9em;
  transition: all 0.25s ease-out;
  background: rgba(160, 82, 45, 0.1);
  color: var(--primary-color);
  position: relative;
  overflow: hidden;
}

.ops button:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.ops button:active {
  transform: translateY(0) scale(0.98);
}

/* 模态框 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(74, 59, 48, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease-out;
}

.modal-container {
  background: var(--surface-color);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  width: 520px;
  max-width: 90vw;
  box-shadow: 0 20px 60px rgba(160, 82, 45, 0.15);
  border: 2px solid var(--accent-cream);
  animation: slideUp 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* 配方管理模态框 */
.recipe-modal {
  width: 600px;
  max-width: 95vw;
}

.recipe-row {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.recipe-summary {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md);
  background: rgba(255, 248, 220, 0.3);
  border-radius: var(--border-radius-lg);
  text-align: right;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', serif;
  border: 1px solid var(--accent-cream);
}

/* 表单样式 */
.admin-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-item label {
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', serif;
  font-size: 1em;
}

/* 表单元素 */
.admin-input,
.admin-select {
  padding: 12px 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  background: rgba(255, 255, 255, 0.8);
  font-family: 'Nunito', sans-serif;
  color: var(--text-color-dark);
  transition: all 0.25s ease-out;
  font-size: 1em;
}

.admin-input:focus,
.admin-select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
  transform: translateY(-1px);
}

.admin-textarea {
  width: 100%;
  min-height: 120px;
  padding: var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  background: rgba(255, 255, 255, 0.8);
  font-family: 'Nunito', sans-serif;
  color: var(--text-color-dark);
  transition: all 0.25s ease-out;
  resize: vertical;
  line-height: 1.6;
}

.admin-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
}

/* 上传区域 */
.upload-box {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-color);
  transition: all 0.3s ease-out;
}

.upload-box:hover {
  border-color: var(--primary-color);
  background: rgba(255, 248, 220, 0.3);
}

.upload-box input[type="file"] {
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  background: var(--primary-light);
  color: var(--primary-dark);
  font-family: 'Nunito', sans-serif;
  cursor: pointer;
  transition: all 0.25s ease-out;
}

.upload-box input[type="file"]:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-1px);
}

.preview-img {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-lg);
  object-fit: cover;
  border: 2px solid var(--accent-cream);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
  transition: all 0.3s ease-out;
}

.preview-img:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

/* 按钮样式 */
.btn-primary,
.btn-success,
.btn-warning,
.btn-danger,
.btn-info {
  padding: 12px 28px;
  border: none;
  border-radius: var(--border-radius-xl);
  cursor: pointer;
  font-family: 'Prompt', serif;
  font-weight: 600;
  font-size: 1em;
  transition: all 0.25s ease-out;
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.btn-success {
  background: linear-gradient(135deg, #2c9678, #5f9e5f);
  color: white;
  box-shadow: 0 6px 20px rgba(44, 150, 120, 0.3);
}

.btn-warning {
  background: linear-gradient(135deg, var(--accent-brown), #b8860b);
  color: white;
  box-shadow: 0 6px 20px rgba(222, 184, 135, 0.3);
}

.btn-danger {
  background: linear-gradient(135deg, #c13c3c, #8b0000);
  color: white;
  box-shadow: 0 6px 20px rgba(193, 60, 60, 0.3);
}

.btn-info {
  background: linear-gradient(135deg, var(--accent-brown), var(--primary-dark));
  color: white;
  box-shadow: 0 6px 20px rgba(139, 69, 19, 0.3);
}

/* 按钮悬停效果 */
.btn-primary:hover,
.btn-success:hover,
.btn-warning:hover,
.btn-danger:hover,
.btn-info:hover {
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.4);
}

.btn-primary:active,
.btn-success:active,
.btn-warning:active,
.btn-danger:active,
.btn-info:active {
  transform: translateY(0) scale(0.98);
}

/* 虚线按钮 */
.btn-dashed {
  border: 2px dashed var(--border-color);
  background: rgba(255, 255, 255, 0.3);
  width: 100%;
  padding: 14px;
  cursor: pointer;
  color: var(--text-color-medium);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  border-radius: var(--border-radius-lg);
  transition: all 0.3s ease-out;
  margin-top: var(--spacing-md);
}

.btn-dashed:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.05);
  transform: translateY(-2px);
}

/* 文本按钮 */
.btn-text-danger {
  color: #c13c3c;
  background: none;
  border: none;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: var(--border-radius-md);
  transition: all 0.25s ease-out;
}

.btn-text-danger:hover {
  background: rgba(193, 60, 60, 0.1);
  transform: translateY(-1px);
}

/* 模态框底部按钮 */
.modal-footer {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.modal-footer button {
  padding: 10px 24px;
  border-radius: var(--border-radius-lg);
  border: none;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  transition: all 0.25s ease-out;
}

.modal-footer button:first-child {
  background: var(--border-color);
  color: var(--text-color-medium);
}

.modal-footer button:first-child:hover {
  background: var(--text-color-medium);
  color: white;
}

/* 复选框样式 */
input[type="checkbox"] {
  width: 20px;
  height: 20px;
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-color);
  cursor: pointer;
  transition: all 0.25s ease-out;
  accent-color: var(--primary-color);
}

input[type="checkbox"]:hover {
  border-color: var(--primary-color);
  transform: scale(1.1);
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-manage {
    padding: var(--spacing-md);
    gap: var(--spacing-lg);
  }

  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-form,
  .batch-actions {
    flex-direction: column;
    width: 100%;
  }

  .admin-input,
  .admin-select,
  .btn-primary,
  .btn-success,
  .btn-warning,
  .btn-danger,
  .btn-info {
    width: 100%;
  }

  .admin-table {
    display: block;
    overflow-x: auto;
  }

  .admin-table th,
  .admin-table td {
    padding: var(--spacing-sm);
    font-size: 0.9em;
  }

  .recipe-row {
    flex-direction: column;
    align-items: stretch;
  }

  .modal-container {
    padding: var(--spacing-md);
    width: 95vw;
  }

  .modal-footer {
    flex-direction: column;
  }

  .modal-footer button {
    width: 100%;
  }

  .upload-box {
    flex-direction: column;
    align-items: stretch;
  }
}

/* 定义CSS变量 */
:root {
  /* 色彩方案 */
  --background-color: #f5f0e1;
  --surface-color: #e8dccb;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --primary-light: #d2b48c;
  --accent-cream: #fff8dc;
  --accent-pink: #ffc0cb;
  --accent-brown: #deb887;
  --text-color-dark: #4a3b30;
  --text-color-medium: #7a6a5b;
  --text-color-light: #a09080;
  --border-color: #d4c7b5;

  /* 圆角大小 */
  --border-radius-sm: 8px;
  --border-radius-md: 12px;
  --border-radius-lg: 20px;
  --border-radius-xl: 30px;

  /* 间距 */
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
}
</style>