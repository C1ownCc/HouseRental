import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/views/client/Layout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/client/Home.vue')
        },
        {
          path: 'house/:id',
          name: 'houseDetail',
          component: () => import('@/views/client/HouseDetail.vue')
        },
        {
          path: 'house-list',
          name: 'houseList',
          component: () => import('@/views/client/HouseList.vue')
        },
        {
          path: 'house/map',
          name: 'mapHouse',
          component: () => import('@/views/client/MapHouse.vue')
        },
        {
          path: 'user',
          name: 'userCenter',
          component: () => import('@/views/client/UserCenter.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'message',
          name: 'messageCenter',
          component: () => import('@/views/client/MessageCenter.vue'),
          meta: { requiresAuth: true }
        },
        // 租约相关路由
        {
          path: 'contracts',
          name: 'contractList',
          component: () => import('@/views/client/ContractList.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'contract/:id',
          name: 'contractDetail',
          component: () => import('@/views/client/ContractDetail.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'contract/:id/payments',
          name: 'paymentList',
          component: () => import('@/views/client/PaymentList.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'payment/success/:paymentId',
          name: 'paymentSuccess',
          component: () => import('@/views/client/PaymentSuccess.vue'),
          meta: { requiresAuth: true }
        },
        // 房东专属路由
        {
          path: 'landlord',
          name: 'landlordCenter',
          component: () => import('@/views/landlord/LandlordCenter.vue'),
          meta: { requiresAuth: true, roles: ['landlord', 'admin'] },
          children: [
            {
              path: 'houses',
              name: 'landlordHouses',
              component: () => import('@/views/landlord/HouseManage.vue'),
              meta: { requiresAuth: true, roles: ['landlord', 'admin'] }
            },
            {
              path: 'bookings',
              name: 'landlordBookings',
              component: () => import('@/views/landlord/BookingManage.vue'),
              meta: { requiresAuth: true, roles: ['landlord', 'admin'] }
            },
            {
              path: 'contracts',
              name: 'landlordContracts',
              component: () => import('@/views/landlord/ContractManage.vue'),
              meta: { requiresAuth: true, roles: ['landlord', 'admin'] }
            }
          ]
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/forgot-password',
      name: 'forgotPassword',
      component: () => import('@/views/ForgotPassword.vue')
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: 'house',
          name: 'houseManage',
          component: () => import('@/views/admin/HouseManage.vue'),
          meta: { title: '房源管理' }
        },
        {
          path: 'user',
          name: 'userManage',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'booking',
          name: 'bookingManage',
          component: () => import('@/views/admin/BookingManage.vue'),
          meta: { title: '预约管理' }
        },
        {
          path: 'contract',
          name: 'contractManage',
          component: () => import('@/views/admin/ContractManage.vue'),
          meta: { title: '合同管理' }
        },
        {
          path: 'contract/detail/:id',
          name: 'adminContractDetail',
          component: () => import('@/views/admin/ContractDetail.vue'),
          meta: { title: '合同详情' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo?.role)) {
    ElMessage.warning('您没有权限访问该页面')
    next('/')
  } else if (to.meta.requiresAdmin && userStore.userInfo?.role !== 'admin') {
    ElMessage.warning('您没有权限访问该页面')
    next('/')
  } else {
    next()
  }
})

export default router
