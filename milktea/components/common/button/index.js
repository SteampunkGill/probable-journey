Component({
  externalClasses: ['custom-class'],
  properties: {
    type: {
      type: String,
      value: 'primary' // primary, secondary, danger, warning
    },
    size: {
      type: String,
      value: 'normal' // large, normal, small, mini
    },
    round: {
      type: Boolean,
      value: true
    },
    plain: {
      type: Boolean,
      value: false
    },
    block: {
      type: Boolean,
      value: false
    },
    disabled: {
      type: Boolean,
      value: false
    },
    loading: {
      type: Boolean,
      value: false
    },
    formType: {
      type: String,
      value: ''
    },
    openType: {
      type: String,
      value: ''
    }
  },
  methods: {
    onTap(e) {
      if (!this.data.disabled && !this.data.loading) {
        this.triggerEvent('click', e.detail);
      }
    }
  }
});