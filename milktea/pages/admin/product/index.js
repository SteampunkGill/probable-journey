const request = require('../../../utils/request');

Page({
  data: {
    products: [],
    filteredProducts: [],
    categories: [{ id: '', name: '全部分类' }],
    categoryIndex: 0,
    ingredients: [],
    selectedIds: [],
    isAllSelected: false,
    filterQuery: {
      name: '',
      categoryId: ''
    },
    editModal: {
      show: false,
      isEdit: false,
      form: {
        id: null,
        name: '',
        categoryId: '',
        price: 0,
        stock: 0,
        alertThreshold: 10,
        imageUrl: '',
        description: '',
        status: 1
      }
    },
    formCategoryIndex: 0,
    recipeModal: {
      show: false,
      product: null,
      list: []
    },
    calculatedCost: '0.00'
  },

  onLoad() {
    this.loadProducts();
    this.loadCategories();
    this.loadIngredients();
  },

  async loadProducts() {
    try {
      const res = await request({ url: '/api/admin/products' });
      const products = (res.data || []).map(p => ({ ...p, selected: false }));
      this.setData({ products }, () => {
        this.applyFilter();
      });
    } catch (error) {
      console.error('加载商品失败:', error);
    }
  },

  async loadCategories() {
    try {
      const res = await request({ url: '/api/admin/categories' });
      const allCats = [{ id: '', name: '全部分类' }];
      const flatten = (list) => {
        list.forEach(c => {
          allCats.push(c);
          if (c.children && c.children.length) flatten(c.children);
        });
      };
      if (res.data) flatten(res.data);
      this.setData({ categories: allCats });
    } catch (error) {
      console.error('加载分类失败:', error);
    }
  },

  async loadIngredients() {
    try {
      const res = await request({ url: '/api/admin/inventory' });
      this.setData({ ingredients: res.data || [] });
    } catch (error) {
      console.error('加载原料失败:', error);
    }
  },

  onSearchInput(e) {
    this.setData({ 'filterQuery.name': e.detail.value }, () => {
      this.applyFilter();
    });
  },

  onCategoryChange(e) {
    const index = e.detail.value;
    this.setData({
      categoryIndex: index,
      'filterQuery.categoryId': this.data.categories[index].id
    }, () => {
      this.applyFilter();
    });
  },

  applyFilter() {
    const { products, filterQuery } = this.data;
    const nameKeyword = filterQuery.name.trim().toLowerCase();
    const categoryId = filterQuery.categoryId;

    const filteredProducts = products.filter(p => {
      const matchName = !nameKeyword || p.name.toLowerCase().includes(nameKeyword);
      const matchCategory = !categoryId || p.categoryId === categoryId;
      return matchName && matchCategory;
    });

    this.setData({ filteredProducts });
    this.updateSelectStatus();
  },

  toggleSelect(e) {
    const id = e.currentTarget.dataset.id;
    const products = this.data.products.map(p => {
      if (p.id === id) p.selected = !p.selected;
      return p;
    });
    this.setData({ products }, () => {
      this.applyFilter();
    });
  },

  toggleAll() {
    const isAllSelected = !this.data.isAllSelected;
    const filteredIds = this.data.filteredProducts.map(p => p.id);
    const products = this.data.products.map(p => {
      if (filteredIds.includes(p.id)) p.selected = isAllSelected;
      return p;
    });
    this.setData({ products, isAllSelected }, () => {
      this.applyFilter();
    });
  },

  updateSelectStatus() {
    const { filteredProducts } = this.data;
    const isAllSelected = filteredProducts.length > 0 && filteredProducts.every(p => p.selected);
    const selectedIds = this.data.products.filter(p => p.selected).map(p => p.id);
    this.setData({ isAllSelected, selectedIds });
  },

  async batchStatus(e) {
    const status = parseInt(e.currentTarget.dataset.status);
    const promises = this.data.selectedIds.map(id =>
      request({ url: `/api/admin/products/${id}/status`, method: 'PUT', data: { status } })
    );
    try {
      await Promise.all(promises);
      wx.showToast({ title: '批量操作成功' });
      this.loadProducts();
    } catch (error) {
      wx.showToast({ title: '操作失败', icon: 'none' });
    }
  },

  async toggleStatus(e) {
    const p = e.currentTarget.dataset.item;
    try {
      await request({
        url: `/api/admin/products/${p.id}/status`,
        method: 'PUT',
        data: { status: p.status === 1 ? 0 : 1 }
      });
      this.loadProducts();
    } catch (error) {
      console.error('切换状态失败:', error);
    }
  },

  showEditModal(e) {
    const p = e.currentTarget.dataset.item;
    if (p) {
      const catIndex = this.data.categories.findIndex(c => c.id === p.categoryId);
      this.setData({
        'editModal.show': true,
        'editModal.isEdit': true,
        'editModal.form': { ...p },
        formCategoryIndex: catIndex > -1 ? catIndex : 0
      });
    } else {
      this.setData({
        'editModal.show': true,
        'editModal.isEdit': false,
        'editModal.form': {
          id: null, name: '', categoryId: '', price: 0, stock: 0,
          alertThreshold: 10, imageUrl: '', description: '', status: 1
        },
        formCategoryIndex: 0
      });
    }
  },

  hideEditModal() {
    this.setData({ 'editModal.show': false });
  },

  onFormInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`editModal.form.${field}`]: e.detail.value });
  },

  onFormCategoryChange(e) {
    const index = e.detail.value;
    this.setData({
      formCategoryIndex: index,
      'editModal.form.categoryId': this.data.categories[index].id
    });
  },

  async handleUpload() {
    try {
      const res = await wx.chooseImage({ count: 1 });
      const tempFilePath = res.tempFilePaths[0];
      // 模拟上传逻辑，实际应调用 wx.uploadFile
      wx.showLoading({ title: '上传中' });
      // 假设后端接口为 /upload/image
      wx.uploadFile({
        url: 'http://localhost:8080/upload/image', // 需替换为实际地址
        filePath: tempFilePath,
        name: 'file',
        success: (uploadRes) => {
          const data = JSON.parse(uploadRes.data);
          this.setData({ 'editModal.form.imageUrl': data.url });
        },
        complete: () => wx.hideLoading()
      });
    } catch (error) {
      console.error('选择图片失败:', error);
    }
  },

  async saveProduct() {
    const { form, isEdit } = this.data.editModal;
    try {
      if (isEdit) {
        await request({ url: `/api/admin/products/${form.id}`, method: 'PUT', data: form });
      } else {
        await request({ url: '/api/admin/products', method: 'POST', data: form });
      }
      this.hideEditModal();
      this.loadProducts();
      wx.showToast({ title: '保存成功' });
    } catch (error) {
      console.error('保存商品失败:', error);
    }
  },

  async showRecipeModal(e) {
    const p = e.currentTarget.dataset.item;
    try {
      const res = await request({ url: `/api/admin/recipes/product/${p.id}` });
      const list = (res.data || []).map(r => {
        const ingIndex = this.data.ingredients.findIndex(i => i.id === r.ingredientId);
        return { ingredientId: r.ingredientId, quantity: r.quantity, ingredientIndex: ingIndex };
      });
      this.setData({
        'recipeModal.show': true,
        'recipeModal.product': p,
        'recipeModal.list': list
      }, () => {
        this.calculateCost();
      });
    } catch (error) {
      console.error('加载配方失败:', error);
    }
  },

  hideRecipeModal() {
    this.setData({ 'recipeModal.show': false });
  },

  addRecipeRow() {
    const list = this.data.recipeModal.list;
    list.push({ ingredientId: '', quantity: 0, ingredientIndex: -1 });
    this.setData({ 'recipeModal.list': list });
  },

  removeRecipeRow(e) {
    const index = e.currentTarget.dataset.index;
    const list = this.data.recipeModal.list;
    list.splice(index, 1);
    this.setData({ 'recipeModal.list': list }, () => {
      this.calculateCost();
    });
  },

  onRecipeIngredientChange(e) {
    const index = e.currentTarget.dataset.index;
    const ingIndex = e.detail.value;
    const ingredient = this.data.ingredients[ingIndex];
    const list = this.data.recipeModal.list;
    list[index].ingredientId = ingredient.id;
    list[index].ingredientIndex = ingIndex;
    this.setData({ 'recipeModal.list': list }, () => {
      this.calculateCost();
    });
  },

  onRecipeQuantityInput(e) {
    const index = e.currentTarget.dataset.index;
    const list = this.data.recipeModal.list;
    list[index].quantity = parseFloat(e.detail.value) || 0;
    this.setData({ 'recipeModal.list': list }, () => {
      this.calculateCost();
    });
  },

  calculateCost() {
    const cost = this.data.recipeModal.list.reduce((total, r) => {
      const ing = this.data.ingredients[r.ingredientIndex];
      return total + (ing ? (ing.costPerUnit || 0) * (r.quantity || 0) : 0);
    }, 0);
    this.setData({ calculatedCost: cost.toFixed(2) });
  },

  async saveRecipe() {
    const { product, list } = this.data.recipeModal;
    try {
      await request({
        url: `/api/admin/recipes/product/${product.id}`,
        method: 'POST',
        data: list.map(r => ({ ingredientId: r.ingredientId, quantity: r.quantity }))
      });
      this.hideRecipeModal();
      wx.showToast({ title: '配方保存成功' });
    } catch (error) {
      console.error('保存配方失败:', error);
    }
  }
});