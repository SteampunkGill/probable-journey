// components/quantity-selector/quantity-selector.js
Component({
  properties: {
    // 当前值
    value: {
      type: Number,
      value: 1,
      observer: function(newVal) {
        // 验证值是否在范围内
        this.validateValue(newVal);
      }
    },
    
    // 最小值
    min: {
      type: Number,
      value: 1
    },
    
    // 最大值
    max: {
      type: Number,
      value: 99
    },
    
    // 是否禁用
    disabled: {
      type: Boolean,
      value: false
    },
    
    // 输入框是否可编辑
    editable: {
      type: Boolean,
      value: true
    },
    
    // 步长（每次增减的数量）
    step: {
      type: Number,
      value: 1
    }
  },

  data: {
    // 内部值
    internalValue: 1
  },

  lifetimes: {
    attached() {
      // 初始化内部值
      this.setData({
        internalValue: this.validateValue(this.properties.value)
      });
    }
  },

  methods: {
    // 验证值是否在范围内
    validateValue(value) {
      const { min, max } = this.properties;
      let validValue = parseInt(value) || min;
      
      if (validValue < min) {
        validValue = min;
      } else if (validValue > max) {
        validValue = max;
      }
      
      return validValue;
    },

    // 减少数量
    decrease() {
      if (this.properties.disabled) return;
      
      const { value, min, step } = this.properties;
      const newValue = Math.max(min, value - step);
      
      if (newValue !== value) {
        this.updateValue(newValue);
      }
    },

    // 增加数量
    increase() {
      if (this.properties.disabled) return;
      
      const { value, max, step } = this.properties;
      const newValue = Math.min(max, value + step);
      
      if (newValue !== value) {
        this.updateValue(newValue);
      }
    },

    // 输入框输入
    onInput(e) {
      if (this.properties.disabled || !this.properties.editable) return;
      
      const inputValue = e.detail.value;
      const validatedValue = this.validateValue(inputValue);
      
      // 更新内部值但不触发change事件（等blur时触发）
      this.setData({
        internalValue: validatedValue
      });
    },

    // 输入框失去焦点
    onBlur(e) {
      if (this.properties.disabled || !this.properties.editable) return;
      
      const validatedValue = this.validateValue(this.data.internalValue);
      
      // 如果值有变化，触发change事件
      if (validatedValue !== this.properties.value) {
        this.updateValue(validatedValue);
      }
    },

    // 更新值并触发事件
    updateValue(newValue) {
      this.setData({
        value: newValue,
        internalValue: newValue
      });
      
      // 触发change事件
      this.triggerEvent('change', {
        value: newValue,
        oldValue: this.properties.value
      });
      
      // 触发自定义事件
      this.triggerEvent('changeValue', newValue);
    },

    // 设置值（外部调用）
    setValue(value) {
      const validatedValue = this.validateValue(value);
      this.updateValue(validatedValue);
    },

    // 重置为最小值
    reset() {
      this.updateValue(this.properties.min);
    }
  }
});