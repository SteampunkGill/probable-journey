// DEMO ONLY: 纯前端 IndexedDB 封装，用于存储促销活动数据
const DB_NAME = 'milktea_demo_db';
const DB_VERSION = 7; // 升级版本以支持活动订单详情存储
const STORE_NAME = 'promotions';
const PRODUCT_STORE = 'products';
const ORDER_STORE = 'orders';
const POINTS_STORE = 'points_records';
const USER_STORE = 'user_info';
const COUPON_STORE = 'user_coupons';
const REFUND_STORE = 'refunds';
const COMPLAINT_STORE = 'complaints';
const PROMO_DETAILS_STORE = 'promo_order_details';

export const initDB = () => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(PRODUCT_STORE)) {
        db.createObjectStore(PRODUCT_STORE, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(ORDER_STORE)) {
        db.createObjectStore(ORDER_STORE, { keyPath: 'orderNo' });
      }
      if (!db.objectStoreNames.contains(POINTS_STORE)) {
        db.createObjectStore(POINTS_STORE, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(USER_STORE)) {
        db.createObjectStore(USER_STORE, { keyPath: 'userId' });
      }
      if (!db.objectStoreNames.contains(COUPON_STORE)) {
        db.createObjectStore(COUPON_STORE, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(REFUND_STORE)) {
        db.createObjectStore(REFUND_STORE, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(COMPLAINT_STORE)) {
        db.createObjectStore(COMPLAINT_STORE, { keyPath: 'id', autoIncrement: true });
      }
      if (!db.objectStoreNames.contains(PROMO_DETAILS_STORE)) {
        db.createObjectStore(PROMO_DETAILS_STORE, { keyPath: 'orderNo' });
      }
    };

    request.onsuccess = (event) => {
      resolve(event.target.result);
    };

    request.onerror = (event) => {
      reject(event.target.error);
    };
  });
};

export const productDB = {
  async getAll() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([PRODUCT_STORE], 'readonly');
      const store = transaction.objectStore(PRODUCT_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async add(product) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([PRODUCT_STORE], 'readwrite');
      const store = transaction.objectStore(PRODUCT_STORE);
      const request = store.add(product);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
};

export const orderDB = {
  async getAll() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([ORDER_STORE], 'readonly');
      const store = transaction.objectStore(ORDER_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getByNo(orderNo) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([ORDER_STORE], 'readonly');
      const store = transaction.objectStore(ORDER_STORE);
      const request = store.get(orderNo);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async add(order) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([ORDER_STORE], 'readwrite');
      const store = transaction.objectStore(ORDER_STORE);
      const request = store.add(order);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async update(order) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([ORDER_STORE], 'readwrite');
      const store = transaction.objectStore(ORDER_STORE);
      const request = store.put(order);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
};

export const pointsDB = {
  async getAllRecords() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([POINTS_STORE], 'readonly');
      const store = transaction.objectStore(POINTS_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async addRecord(record) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([POINTS_STORE], 'readwrite');
      const store = transaction.objectStore(POINTS_STORE);
      const request = store.add({
        ...record,
        createTime: new Date().toISOString()
      });
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getUserPoints(userId = 'default_user') {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([USER_STORE], 'readonly');
      const store = transaction.objectStore(USER_STORE);
      const request = store.get(userId);
      request.onsuccess = () => resolve(request.result ? request.result.points : 1000); // 默认给1000积分演示
      request.onerror = () => reject(request.error);
    });
  },
  async updateUserPoints(points, userId = 'default_user') {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([USER_STORE], 'readwrite');
      const store = transaction.objectStore(USER_STORE);
      const request = store.put({ userId, points });
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getAllCoupons() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([COUPON_STORE], 'readonly');
      const store = transaction.objectStore(COUPON_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async addCoupon(coupon) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([COUPON_STORE], 'readwrite');
      const store = transaction.objectStore(COUPON_STORE);
      const request = store.add(coupon);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
};

export const promotionDB = {
  async getAll() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([STORE_NAME], 'readonly');
      const store = transaction.objectStore(STORE_NAME);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },

  async add(promotion) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([STORE_NAME], 'readwrite');
      const store = transaction.objectStore(STORE_NAME);
      const request = store.add(promotion);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },

  async update(promotion) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([STORE_NAME], 'readwrite');
      const store = transaction.objectStore(STORE_NAME);
      const request = store.put(promotion);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },

  async delete(id) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([STORE_NAME], 'readwrite');
      const store = transaction.objectStore(STORE_NAME);
      const request = store.delete(id);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },

  // 初始化模拟数据
  async seed() {
    // DEMO ONLY: 强制重置数据以确保演示可见
    const db = await initDB();
    
    // 检查 store 是否存在，防止 transaction 报错
    if (!db.objectStoreNames.contains(ORDER_STORE) || 
        !db.objectStoreNames.contains(STORE_NAME) || 
        !db.objectStoreNames.contains(PRODUCT_STORE)) {
      console.warn('IndexedDB stores not ready, skipping seed');
      return;
    }

    await new Promise((resolve, reject) => {
      const transaction = db.transaction([STORE_NAME, PRODUCT_STORE, ORDER_STORE], 'readwrite');
      transaction.objectStore(STORE_NAME).clear();
      transaction.objectStore(PRODUCT_STORE).clear();
      transaction.objectStore(ORDER_STORE).clear();
      transaction.oncomplete = () => resolve();
      transaction.onerror = () => reject(transaction.error);
    });

    // 注入模拟订单
    const demoOrders = [
      {
        orderNo: 'DEMO' + Date.now(),
        status: 'MAKING', // 制作中
        statusText: '正在制作中',
        createTime: new Date().toISOString(),
        estimatedTime: '15', // 预计15分钟
        totalAmount: 22.0,
        deliveryType: 'pickup',
        orderItems: [
          { id: 1000, productName: '初恋的味道', price: 22.0, quantity: 1, image: 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400' }
        ],
        progress: [
          { title: '已下单', time: '刚刚', completed: true },
          { title: '制作中', time: '刚刚', completed: true },
          { title: '待取餐', time: '', completed: false },
          { title: '已完成', time: '', completed: false }
        ],
        storeName: '王大妈的奶茶店（总店）',
        pickupCode: 'A888'
      }
    ];

    for (const o of demoOrders) {
      await orderDB.add(o);
    }

    // 注入模拟商品
    const demoProducts = [
      {
        id: 1000,
        name: "初恋的味道",
        price: 22.0,
        description: "酸酸甜甜的草莓果肉，搭配清爽的绿茶底。",
        image: "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400",
        sales: 666,
        isHot: true,
        categoryId: 'all'
      }
    ];

    for (const p of demoProducts) {
      await productDB.add(p);
    }

    const list = await this.getAll();
    if (list.length === 0) {
      // 初始活动 1：经典奶茶 8 折
      await this.add({
        name: '经典奶茶限时 8 折',
        type: 'PROMOTION_DISCOUNT',
        rulesJson: JSON.stringify({
          discount: 0.8,
          productIds: [1, 2, 3, 4, 5]
        }),
        isActive: true,
        startTime: new Date().toISOString(),
        endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
        description: '王大妈的奶茶系列参与活动'
      });

      // 初始活动 2：冬季新品尝鲜 9 折
      await this.add({
        name: '冬季新品尝鲜 9 折',
        type: 'PROMOTION_DISCOUNT',
        rulesJson: JSON.stringify({
          discount: 0.9,
          productIds: [6, 7, 8]
        }),
        isActive: true,
        startTime: new Date().toISOString(),
        endTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
        description: '暖冬新品，限时尝鲜'
      });
    }
  }
};

export const promoOrderDB = {
  async saveDetails(details) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([PROMO_DETAILS_STORE], 'readwrite');
      const store = transaction.objectStore(PROMO_DETAILS_STORE);
      const request = store.put(details);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getDetails(orderNo) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([PROMO_DETAILS_STORE], 'readonly');
      const store = transaction.objectStore(PROMO_DETAILS_STORE);
      const request = store.get(orderNo);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
};

export const complaintDB = {
  async add(complaint) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([COMPLAINT_STORE], 'readwrite');
      const store = transaction.objectStore(COMPLAINT_STORE);
      complaint.createTime = new Date().toISOString();
      const request = store.add(complaint);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getAll() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([COMPLAINT_STORE], 'readonly');
      const store = transaction.objectStore(COMPLAINT_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime)));
      request.onerror = () => reject(request.error);
    });
  }
};

export const refundDB = {
  async add(refund) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([REFUND_STORE], 'readwrite');
      const store = transaction.objectStore(REFUND_STORE);
      if (!refund.createTime) {
        refund.createTime = new Date().toISOString();
      }
      const request = store.put(refund);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getAll() {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([REFUND_STORE], 'readonly');
      const store = transaction.objectStore(REFUND_STORE);
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime)));
      request.onerror = () => reject(request.error);
    });
  }
};

export const userDB = {
  async saveUserInfo(userInfo) {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([USER_STORE], 'readwrite');
      const store = transaction.objectStore(USER_STORE);
      const data = {
        userId: userInfo.id || userInfo.userId || 'current_user',
        ...userInfo,
        lastUpdated: new Date().toISOString()
      };
      const request = store.put(data);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  },
  async getUserInfo(userId = 'current_user') {
    const db = await initDB();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction([USER_STORE], 'readonly');
      const store = transaction.objectStore(USER_STORE);
      const request = store.get(userId);
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }
};