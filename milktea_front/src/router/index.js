import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'index',
    component: () => import('../views/index/index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/order',
    name: 'order',
    component: () => import('../views/order/order.vue'),
    meta: { title: '点单' }
  },
  {
    path: '/cart',
    name: 'cart',
    component: () => import('../views/cart/index.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/user',
    name: 'user',
    component: () => import('../views/user/user.vue'),
    meta: { title: '我的', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/profile/profile.vue'),
    meta: { title: '个人资料', requiresAuth: true }
  },
  {
    path: '/user/bind-card',
    name: 'bind-card',
    component: () => import('../views/user/bind-card.vue'),
    meta: { title: '绑定会员卡', requiresAuth: true }
  },
  {
    path: '/pickup',
    name: 'pickup',
    component: () => import('../views/pickup/pickup.vue'),
    meta: { title: '取餐' }
  },
  {
    path: '/address',
    name: 'address',
    component: () => import('../views/address/address.vue'),
    meta: { title: '地址管理' }
  },
  {
    path: '/address/edit',
    name: 'address-edit',
    component: () => import('../views/address/edit.vue'),
    meta: { title: '编辑地址' }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/login/register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/product/:id',
    name: 'product',
    component: () => import('../views/product/index.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('../views/search/search.vue'),
    meta: { title: '搜索' }
  },
  {
    path: '/order/checkout',
    name: 'checkout',
    component: () => import('../views/order/checkout.vue'),
    meta: { title: '结算' }
  },
  {
    path: '/payment',
    name: 'payment',
    component: () => import('../views/payment/payment.vue'),
    meta: { title: '支付' }
  },
  {
    path: '/order-list',
    name: 'order-list',
    component: () => import('../views/order-list/order-list.vue'),
    meta: { title: '订单列表' }
  },
  {
    path: '/order-detail/:id',
    name: 'order-detail',
    component: () => import('../views/order/order-detail.vue'),
    meta: { title: '订单详情' }
  },
  {
    path: '/review/:id',
    name: 'review',
    component: () => import('../views/review/review.vue'),
    meta: { title: '评价订单', requiresAuth: true }
  },
  {
    path: '/coupon',
    name: 'coupon',
    component: () => import('../views/coupon/coupon.vue'),
    meta: { title: '优惠券' }
  },
  {
    path: '/share',
    name: 'share',
    component: () => import('../views/share/share.vue'),
    meta: { title: '分享有礼', requiresAuth: true }
  },
  {
    path: '/wallet',
    name: 'wallet',
    component: () => import('../views/wallet/wallet.vue'),
    meta: { title: '钱包' }
  },
  {
    path: '/wallet/gift-card',
    name: 'gift-card',
    component: () => import('../views/wallet/gift-card.vue'),
    meta: { title: '礼品卡', requiresAuth: true }
  },
  {
    path: '/points',
    name: 'points',
    component: () => import('../views/points/points.vue'),
    meta: { title: '积分' }
  },
  {
    path: '/points/mall',
    name: 'points-mall',
    component: () => import('../views/points/mall/mall.vue'),
    meta: { title: '积分商城' }
  },
  {
    path: '/favorite',
    name: 'favorite',
    component: () => import('../views/favorite/favorite.vue'),
    meta: { title: '收藏' }
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('../views/settings/settings.vue'),
    meta: { title: '设置', requiresAuth: true }
  },
  {
    path: '/settings/security',
    name: 'security',
    component: () => import('../views/settings/security.vue'),
    meta: { title: '账号安全', requiresAuth: true }
  },
  {
    path: '/settings/change-password',
    name: 'change-password',
    component: () => import('../views/settings/change-password.vue'),
    meta: { title: '修改密码', requiresAuth: true }
  },
  {
    path: '/about',
    name: 'about-root',
    component: () => import('../views/settings/about.vue'),
    meta: { title: '关于我们' }
  },
  {
    path: '/admin',
    name: 'admin-layout',
    component: () => import('../views/admin/layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('../views/admin/dashboard/index.vue'),
        meta: { title: '仪表盘', requiresAdmin: true }
      },
      {
        path: 'product',
        name: 'admin-product',
        component: () => import('../views/admin/product/index.vue'),
        meta: { title: '商品管理', requiresAdmin: true }
      },
      {
        path: 'inventory',
        name: 'admin-inventory',
        component: () => import('../views/admin/inventory/index.vue'),
        meta: { title: '库存管理', requiresAdmin: true }
      },
      {
        path: 'store',
        name: 'admin-store',
        component: () => import('../views/admin/store/index.vue'),
        meta: { title: '门店管理', requiresAdmin: true }
      },
      {
        path: 'order',
        name: 'admin-order',
        component: () => import('../views/admin/order/index.vue'),
        meta: { title: '订单管理', requiresAdmin: true }
      },
      {
        path: 'member',
        name: 'admin-member',
        component: () => import('../views/admin/member/index.vue'),
        meta: { title: '会员管理', requiresAdmin: true }
      },
      {
        path: 'marketing',
        name: 'admin-marketing',
        component: () => import('../views/admin/marketing/index.vue'),
        meta: { title: '营销管理', requiresAdmin: true }
      },
      {
        path: 'system',
        name: 'admin-system',
        component: () => import('../views/admin/system/index.vue'),
        meta: { title: '系统管理', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }

  const rawToken = localStorage.getItem('token')
  const token = (rawToken && rawToken !== 'undefined' && rawToken !== 'null') ? rawToken : null
  
  let userInfo = {}
  try {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo && storedUserInfo !== 'undefined' && storedUserInfo !== 'null') {
      userInfo = JSON.parse(storedUserInfo)
    }
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth && !token) {
    // 如果已经在登录页，不要重复跳转
    if (to.path === '/login') {
      return next()
    }
    return next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  }

  // 检查是否需要管理员权限
  // 只要路由链条中任何一个 meta 包含 requiresAdmin，就需要检查
  const needsAdmin = to.matched.some(record => record.meta.requiresAdmin)
  if (needsAdmin) {
    if (!token) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
    if (userInfo.role !== 'admin') {
      alert('权限不足，无法访问管理后台')
      return next({ path: '/' })
    }
  }

  next()
})

export default router