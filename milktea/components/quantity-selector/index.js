Component({
  /**
   * 组件的属性列表
   */
  properties: {
    value: {
      type: Number,
      value: 1
    },
    min: {
      type: Number,
      value: 1
    },
    max: {
      type: Number,
      value: 99
    },
    disabled: {
      type: Boolean,
      value: false
    },
    editable: {
      type: Boolean,
      value: true
    },
    step: {
      type: Number,
      value: 1
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    validateValue(val) {
      let validValue = parseInt(val) || this.properties.min;
      if (validValue < this.properties.min) {
        validValue = this.properties.min;
      } else if (validValue > this.properties.max) {
        validValue = this.properties.max;
      }
      return validValue;
    },

    decrease() {
      if (this.properties.disabled || this.properties.value <= this.properties.min) return;
      const newValue = Math.max(this.properties.min, this.properties.value - this.properties.step);
      this.updateValue(newValue);
    },

    increase() {
      if (this.properties.disabled || this.properties.value >= this.properties.max) return;
      const newValue = Math.min(this.properties.max, this.properties.value + this.properties.step);
      this.updateValue(newValue);
    },

    onInput(e) {
      if (this.properties.disabled || !this.properties.editable) return;
      const val = this.validateValue(e.detail.value);
      this.updateValue(val);
    },

    onBlur(e) {
      if (this.properties.disabled || !this.properties.editable) return;
      const val = this.validateValue(e.detail.value);
      this.updateValue(val);
    },

    updateValue(newValue) {
      if (newValue === this.properties.value) return;
      this.triggerEvent('change', {
        value: newValue,
        oldValue: this.properties.value
      });
    }
  }
})