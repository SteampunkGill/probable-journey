Component({
  /**
   * 组件的属性列表
   */
  properties: {
    product: {
      type: Object,
      value: {}
    },
    isFavorite: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    onTap() {
      this.triggerEvent('click', { id: this.properties.product.id });
    },

    onFavoriteTap() {
      const newStatus = !this.properties.isFavorite;
      this.triggerEvent('favorite-change', { 
        id: this.properties.product.id, 
        isFavorite: newStatus 
      });
    },

    onAddToCart() {
      this.triggerEvent('add-to-cart', this.properties.product);
    }
  }
})