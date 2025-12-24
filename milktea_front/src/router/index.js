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
    meta: { title: '我的' }
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
    path: '/coupon',
    name: 'coupon',
    component: () => import('../views/coupon/coupon.vue'),
    meta: { title: '优惠券' }
  },
  {
    path: '/wallet',
    name: 'wallet',
    component: () => import('../views/wallet/wallet.vue'),
    meta: { title: '钱包' }
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
  }
  ,
  {
    path: '/admin',
    name: 'admin-layout',
    component: () => import('../views/admin/layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('../views/admin/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'product',
        name: 'admin-product',
        component: () => import('../views/admin/product/index.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'order',
        name: 'admin-order',
        component: () => import('../views/admin/order/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'member',
        name: 'admin-member',
        component: () => import('../views/admin/member/index.vue'),
        meta: { title: '会员管理' }
      },
      {
        path: 'marketing',
        name: 'admin-marketing',
        component: () => import('../views/admin/marketing/index.vue'),
        meta: { title: '营销管理' }
      },
      {
        path: 'system',
        name: 'admin-system',
        component: () => import('../views/admin/system/index.vue'),
        meta: { title: '系统管理' }
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
  next()
})

export default router