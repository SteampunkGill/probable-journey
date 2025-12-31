<template>
  <div class="marketing-manage">
    <div class="marketing-tabs card">
      <div class="tab-item" :class="{ active: activeTab === 'activity' }" @click="activeTab = 'activity'">活动管理</div>
      <div class="tab-item" :class="{ active: activeTab === 'coupon' }" @click="activeTab = 'coupon'">优惠券管理</div>
      <div class="tab-item" :class="{ active: activeTab === 'banner' }" @click="activeTab = 'banner'">广告/轮播图</div>
      <div class="tab-item" :class="{ active: activeTab === 'push' }" @click="activeTab = 'push'">消息推送</div>
    </div>

    <div class="content-container card">
      <!-- 活动管理 -->
      <div v-if="activeTab === 'activity'" class="activity-section">
        <div class="section-header">
          <button class="btn-success" @click="showActivityModal()">创建活动</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>活动名称</th>
              <th>类型</th>
              <th>时间范围</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in activities" :key="a.id">
              <td>{{ a.name }}</td>
              <td>
                <span v-if="a.type === 'FULL_REDUCE'">满减活动</span>
                <span v-else-if="a.type === 'SECOND_HALF'">第二杯半价</span>
                <span v-else-if="a.type === 'LIMITED_DISCOUNT'">限时折扣</span>
                <span v-else-if="a.type === 'FLASH_SALE'">限时秒杀</span>
                <span v-else>{{ a.type }}</span>
              </td>
              <td>{{ formatDate(a.startTime) }} ~ {{ formatDate(a.endTime) }}</td>
              <td>
                <span :class="a.isActive ? 'status-on' : 'status-off'">{{ a.isActive ? '进行中' : '已停止' }}</span>
              </td>
              <td class="ops">
                <button @click="toggleActivity(a)">{{ a.isActive ? '停止' : '启动' }}</button>
                <button @click="showActivityModal(a)">编辑</button>
                <button @click="viewEffect(a)">效果分析</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 优惠券管理 -->
      <div v-else-if="activeTab === 'coupon'" class="coupon-section">
        <div class="section-header">
          <button class="btn-success" @click="showCouponModal()">新增模板</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>模板名称</th>
              <th>类型</th>
              <th>面值/折扣</th>
              <th>门槛</th>
              <th>已领/总量</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="t in couponTemplates" :key="t.id">
              <td>{{ t.name }}</td>
              <td>{{ t.type === 'REDUCTION' ? '满减' : '折扣' }}</td>
              <td>{{ t.type === 'REDUCTION' ? '¥' + t.value : t.value + '折' }}</td>
              <td>¥{{ t.minAmount }}</td>
              <td>{{ t.issuedQuantity || 0 }}/{{ t.totalQuantity === -1 ? '无限制' : t.totalQuantity }}</td>
              <td class="ops">
                <button @click="distributeCoupon(t)">精准发放</button>
                <button @click="viewDistribution(t)">发放记录</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 广告管理 -->
      <div v-else-if="activeTab === 'banner'" class="banner-section">
        <div class="section-header">
          <button class="btn-success" @click="showBannerModal()">新增轮播图</button>
        </div>
        <div class="banner-grid">
          <div v-for="b in banners" :key="b.id" class="banner-card">
            <img :src="b.imageUrl" class="banner-img" />
            <div class="banner-info">
              <p>位置：{{ b.position }}</p>
              <p>排序：{{ b.sort }}</p>
            </div>
            <div class="banner-ops">
              <button @click="showBannerModal(b)">编辑</button>
              <button class="text-danger" @click="deleteBanner(b)">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息推送 -->
      <div v-else-if="activeTab === 'push'" class="push-section">
        <div class="section-header">
          <button class="btn-success" @click="showPushModal()">新建推送任务</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>标题</th>
              <th>类型</th>
              <th>目标</th>
              <th>触发方式</th>
              <th>状态</th>
              <th>发送时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in pushTasks" :key="p.id">
              <td>{{ p.title }}</td>
              <td>{{ p.pushType === 'MARKETING' ? '精准营销' : '活动通知' }}</td>
              <td>{{ p.targetType }} ({{ p.targetValue || '全部' }})</td>
              <td>{{ p.triggerType }}</td>
              <td>{{ p.status }}</td>
              <td>{{ formatDate(p.sentTime) }}</td>
              <td class="ops">
                <button @click="viewPushStats(p)">效果分析</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>

    <!-- 推送编辑弹窗 -->
    <div v-if="pushModal.show" class="modal-mask">
      <div class="modal-container" style="width: 500px;">
        <h3>新建推送任务</h3>
        <div class="form-item">
          <label>推送标题：</label>
          <input v-model="pushModal.form.title" class="admin-input" placeholder="请输入推送标题" />
        </div>
        <div class="form-item">
          <label>推送内容：</label>
          <textarea v-model="pushModal.form.content" class="admin-input" rows="3" placeholder="请输入推送内容"></textarea>
        </div>
        <div class="form-item">
          <label>推送类型：</label>
          <select v-model="pushModal.form.pushType" class="admin-select">
            <option value="MARKETING">精准营销推送</option>
            <option value="ACTIVITY">活动通知发送</option>
          </select>
        </div>
        <div class="form-item">
          <label>目标人群：</label>
          <select v-model="pushModal.form.targetType" class="admin-select">
            <option value="ALL">全部用户</option>
            <option value="MEMBER_LEVEL">指定会员等级</option>
            <option value="INDIVIDUAL">指定用户ID</option>
          </select>
        </div>
        <div v-if="pushModal.form.targetType !== 'ALL'" class="form-item">
          <label>目标值：</label>
          <input v-model="pushModal.form.targetValue" class="admin-input" placeholder="等级ID或用户ID(逗号隔开)" />
        </div>
        <div class="form-item">
          <label>触发方式：</label>
          <select v-model="pushModal.form.triggerType" class="admin-select">
            <option value="IMMEDIATE">立即发送</option>
            <option value="SCHEDULED">定时发送</option>
            <option value="BEHAVIOR_TRIGGER">行为触发</option>
          </select>
        </div>
        <div v-if="pushModal.form.triggerType === 'SCHEDULED'" class="form-item">
          <label>计划发送时间：</label>
          <input v-model="pushModal.form.scheduledTime" type="datetime-local" class="admin-input" />
        </div>
        <div v-if="pushModal.form.triggerType === 'BEHAVIOR_TRIGGER'" class="form-item">
          <label>触发行为：</label>
          <input v-model="pushModal.form.triggerCondition" class="admin-input" placeholder="如：ORDER_COMPLETED" />
        </div>
        <div class="form-item">
          <label>配图URL：</label>
          <input v-model="pushModal.form.imageUrl" class="admin-input" placeholder="可选" />
        </div>
        <div class="form-item">
          <label>跳转链接：</label>
          <input v-model="pushModal.form.linkUrl" class="admin-input" placeholder="可选" />
        </div>
        <div class="modal-footer">
          <button @click="pushModal.show = false">取消</button>
          <button class="btn-primary" @click="savePushTask">提交推送</button>
        </div>
      </div>
    </div>

    <!-- 活动编辑弹窗 -->
    <div v-if="activityModal.show" class="modal-mask">
      <div class="modal-container" style="width: 500px;">
        <h3>{{ activityModal.isEdit ? '编辑活动' : '创建活动' }}</h3>
        <div class="form-item">
          <label>活动名称：</label>
          <input v-model="activityModal.form.name" class="admin-input" placeholder="请输入活动名称" />
        </div>
        <div class="form-item">
          <label>活动类型：</label>
          <select v-model="activityModal.form.type" class="admin-select">
            <option value="FULL_REDUCE">满减活动</option>
            <option value="SECOND_HALF">第二杯半价</option>
            <option value="LIMITED_DISCOUNT">限时折扣</option>
            <option value="FLASH_SALE">限时秒杀</option>
            <option value="PROMOTION_DISCOUNT">指定商品折扣 (IndexedDB)</option>
          </select>
        </div>
        <div class="form-item">
          <label>开始时间：</label>
          <input v-model="activityModal.form.startTime" type="datetime-local" class="admin-input" />
        </div>
        <div class="form-item">
          <label>结束时间：</label>
          <input v-model="activityModal.form.endTime" type="datetime-local" class="admin-input" />
        </div>
        <!-- 活动规则可视化配置 -->
        <div class="activity-rules-config">
          <div v-if="activityModal.form.type === 'FULL_REDUCE'">
            <div class="form-item">
              <label>满减门槛 (元)：</label>
              <input type="number" v-model.number="activityRules.threshold" class="admin-input" placeholder="例如：30" />
            </div>
            <div class="form-item">
              <label>减免金额 (元)：</label>
              <input type="number" v-model.number="activityRules.reduce" class="admin-input" placeholder="例如：5" />
            </div>
          </div>
          
          <div v-else-if="activityModal.form.type === 'SECOND_HALF'">
            <div class="form-item">
              <label>第二杯折扣率：</label>
              <input type="number" v-model.number="activityRules.secondDiscount" class="admin-input" placeholder="例如：0.5 (代表半价)" />
            </div>
          </div>

          <div v-else-if="activityModal.form.type === 'LIMITED_DISCOUNT'">
            <div class="form-item">
              <label>折扣比例 (0-1)：</label>
              <input type="number" v-model.number="activityRules.discount" class="admin-input" placeholder="例如：0.8 (代表8折)" />
            </div>
          </div>

          <div v-else-if="activityModal.form.type === 'FLASH_SALE'">
            <div class="form-item">
              <label>秒杀价格 (元)：</label>
              <input type="number" v-model.number="activityRules.flashPrice" class="admin-input" placeholder="请输入秒杀价" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="activityModal.show = false">取消</button>
          <button class="btn-primary" @click="saveActivity">保存</button>
        </div>
      </div>
    </div>

    <!-- 轮播图编辑弹窗 -->
    <div v-if="bannerModal.show" class="modal-mask">
      <div class="modal-container" style="width: 500px;">
        <h3>{{ bannerModal.isEdit ? '编辑轮播图' : '新增轮播图' }}</h3>
        <div class="form-item">
          <label>图片：</label>
          <div class="upload-container">
            <input type="file" ref="bannerFileInput" style="display: none" accept="image/*" @change="handleBannerFileChange" />
            <div class="upload-preview" @click="$refs.bannerFileInput.click()">
              <img v-if="bannerModal.form.imageUrl" :src="bannerModal.form.imageUrl" class="preview-img" />
              <div v-else class="upload-placeholder">
                <span>+</span>
                <p>点击上传图片</p>
              </div>
            </div>
            <input v-model="bannerModal.form.imageUrl" class="admin-input" style="margin-top: 10px;" placeholder="或手动输入图片URL" />
          </div>
        </div>
        <div class="form-item">
          <label>显示位置：</label>
          <select v-model="bannerModal.form.position" class="admin-select">
            <option value="HOME">首页</option>
          </select>
        </div>
        <div class="form-item">
          <label>排序：</label>
          <input v-model.number="bannerModal.form.sort" type="number" class="admin-input" />
        </div>
        <div class="form-item">
          <label>跳转类型：</label>
          <select v-model="bannerModal.form.linkType" class="admin-select">
            <option value="NONE">不跳转</option>
            <option value="PRODUCT">商品详情</option>
            <option value="URL">外部链接</option>
          </select>
        </div>
        <div v-if="bannerModal.form.linkType !== 'NONE'" class="form-item">
          <label>跳转值 (商品ID或URL)：</label>
          <input v-model="bannerModal.form.linkValue" class="admin-input" />
        </div>
        <div class="modal-footer">
          <button @click="bannerModal.show = false">取消</button>
          <button class="btn-primary" @click="saveBanner">保存</button>
        </div>
      </div>
    </div>
    <!-- 优惠券发放弹窗 -->
    <div v-if="distributeModal.show" class="modal-mask">
      <div class="modal-container" style="width: 600px;">
        <h3>精准发放优惠券</h3>
        <p style="margin-bottom: 15px; color: #8b4513;">正在为 [{{ distributeModal.template?.name }}] 选择发放对象</p>
        
        <div class="member-selector-box" style="max-height: 400px; overflow-y: auto; border: 2px solid #e8dccb; border-radius: 15px; padding: 10px; background: #fff;">
          <div v-if="distributeModal.loading" style="text-align: center; padding: 20px;">加载中...</div>
          <table v-else class="admin-table" style="box-shadow: none;">
            <thead>
              <tr>
                <th style="width: 40px;"><input type="checkbox" @change="toggleAllMembers" :checked="isAllSelected" /></th>
                <th>昵称</th>
                <th>手机号</th>
                <th>等级</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in distributeModal.members" :key="m.id" @click="toggleMemberSelection(m.id)" style="cursor: pointer;">
                <td><input type="checkbox" :value="m.id" v-model="distributeModal.selectedIds" @click.stop /></td>
                <td>{{ m.nickname }}</td>
                <td>{{ m.phone }}</td>
                <td>{{ m.levelName }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div style="margin-top: 15px; font-size: 14px; color: #7a6a5b;">
          已选择 {{ distributeModal.selectedIds.length }} 位会员
        </div>

        <div class="modal-footer">
          <button @click="distributeModal.show = false">取消</button>
          <button class="btn-primary" @click="confirmDistribute" :disabled="distributeModal.selectedIds.length === 0">确认发放</button>
        </div>
      </div>
    </div>

    <!-- 优惠券模板编辑弹窗 -->
    <div v-if="couponModal.show" class="modal-mask">
      <div class="modal-container" style="width: 600px;">
        <h3>{{ couponModal.isEdit ? '编辑优惠券模板' : '新增优惠券模板' }}</h3>
        <div class="form-scroll-container" style="max-height: 70vh; overflow-y: auto; padding-right: 10px;">
          <div class="form-item">
            <label>模板名称：</label>
            <input v-model="couponModal.form.name" class="admin-input" placeholder="请输入模板名称" />
          </div>
          <div class="form-item">
            <label>优惠类型：</label>
            <select v-model="couponModal.form.type" class="admin-select">
              <option value="REDUCTION">满减</option>
              <option value="DISCOUNT">折扣</option>
            </select>
          </div>
          <div class="form-item">
            <label>{{ couponModal.form.type === 'REDUCTION' ? '减免金额 (元)：' : '折扣比例 (0-10，如8.5折输入8.5)：' }}</label>
            <input type="number" v-model.number="couponModal.form.value" class="admin-input" :placeholder="couponModal.form.type === 'REDUCTION' ? '例如：5' : '例如：8.5'" />
          </div>
          <div class="form-item">
            <label>使用门槛 (元)：</label>
            <input type="number" v-model.number="couponModal.form.minAmount" class="admin-input" placeholder="例如：30" />
          </div>
          <div class="form-item">
            <label>发行总量：</label>
            <input type="number" v-model.number="couponModal.form.totalQuantity" class="admin-input" placeholder="-1表示不限制" />
          </div>
          <div class="form-item">
            <label>每人限领：</label>
            <input type="number" v-model.number="couponModal.form.usageLimitPerUser" class="admin-input" placeholder="默认1" />
          </div>
          <div class="form-item">
            <label>有效期类型：</label>
            <select v-model="couponModal.form.validityType" class="admin-select">
              <option value="FIXED_DAYS">领取后固定天数有效</option>
              <option value="FIXED_PERIOD">固定日期范围</option>
            </select>
          </div>
          <div v-if="couponModal.form.validityType === 'FIXED_DAYS'" class="form-item">
            <label>有效天数：</label>
            <input type="number" v-model.number="couponModal.form.validityDays" class="admin-input" placeholder="例如：7" />
          </div>
          <div v-if="couponModal.form.validityType === 'FIXED_PERIOD'" class="form-item">
            <label>开始时间：</label>
            <input v-model="couponModal.form.startTime" type="datetime-local" class="admin-input" />
          </div>
          <div v-if="couponModal.form.validityType === 'FIXED_PERIOD'" class="form-item">
            <label>结束时间：</label>
            <input v-model="couponModal.form.endTime" type="datetime-local" class="admin-input" />
          </div>
          
          <!-- 优惠券预览可视化 -->
          <div class="coupon-preview-box">
            <label>样式预览：</label>
            <div class="coupon-card-preview" :class="couponModal.form.type">
              <div class="coupon-left">
                <div class="coupon-value">
                  <span v-if="couponModal.form.type === 'REDUCTION'">¥</span>
                  <span class="num">{{ couponModal.form.value || '0' }}</span>
                  <span v-if="couponModal.form.type === 'DISCOUNT'">折</span>
                </div>
                <div class="coupon-threshold">满{{ couponModal.form.minAmount || '0' }}可用</div>
              </div>
              <div class="coupon-right">
                <div class="coupon-name">{{ couponModal.form.name || '优惠券名称' }}</div>
                <div class="coupon-time">
                  <span v-if="couponModal.form.validityType === 'FIXED_DAYS'">领取后{{ couponModal.form.validityDays || 'X' }}天内有效</span>
                  <span v-else>{{ couponModal.form.startTime ? couponModal.form.startTime.substring(0,10) : '开始日期' }} 至 {{ couponModal.form.endTime ? couponModal.form.endTime.substring(0,10) : '结束日期' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="couponModal.show = false">取消</button>
          <button class="btn-primary" @click="saveCouponTemplate">保存模板</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { get, post, put, del, uploadFile } from '../../../utils/request'
import { promotionApi } from '../../../utils/api'
import { complaintDB, refundDB } from '../../../utils/db'

const activeTab = ref('activity')
const activities = ref([])
const couponTemplates = ref([])
const banners = ref([])
const pushTasks = ref([])
const complaints = ref([])
const refunds = ref([])

const activityModal = ref({ show: false, isEdit: false, form: {} })
const activityRules = ref({})
const couponModal = ref({ show: false, isEdit: false, form: { type: 'REDUCTION', validityType: 'FIXED_DAYS', usageLimitPerUser: 1 } })

const showCouponModal = (t = null) => {
  if (t) {
    couponModal.value.isEdit = true
    couponModal.value.form = {
      ...t,
      startTime: t.startTime ? t.startTime.replace(' ', 'T').substring(0, 16) : '',
      endTime: t.endTime ? t.endTime.replace(' ', 'T').substring(0, 16) : ''
    }
  } else {
    couponModal.value.isEdit = false
    couponModal.value.form = { name: '', type: 'REDUCTION', value: 0, minAmount: 0, totalQuantity: -1, validityType: 'FIXED_DAYS', validityDays: 7, usageLimitPerUser: 1 }
  }
  couponModal.value.show = true
}

const saveCouponTemplate = async () => {
  const submitData = { ...couponModal.value.form }
  if (submitData.validityType === 'FIXED_PERIOD') {
    submitData.startTime = formatDateTime(submitData.startTime)
    submitData.endTime = formatDateTime(submitData.endTime)
  } else {
    submitData.startTime = null
    submitData.endTime = null
  }

  try {
    // 补充后端数据库要求的必填字段，确保兼容性
    submitData.acquireLimit = submitData.usageLimitPerUser || 1
    submitData.isActive = true
    submitData.usageScope = 'ALL'
    submitData.remainingQuantity = (submitData.totalQuantity && submitData.totalQuantity !== -1) ? submitData.totalQuantity : 999999

    if (couponModal.value.isEdit) {
      await post('/api/admin/coupon-templates', submitData)
    } else {
      await post('/api/admin/coupon-templates', submitData)
    }
    couponModal.value.show = false
    loadCoupons()
    alert('保存成功')
  } catch (error) {
    console.error('保存优惠券模板失败:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

const pushModal = ref({ show: false, form: { pushType: 'MARKETING', targetType: 'ALL', triggerType: 'IMMEDIATE' } })
const bannerModal = ref({ show: false, isEdit: false, form: {} })

// 监听活动类型变化，重置规则
watch(() => activityModal.value.form.type, (newType) => {
  if (!activityModal.value.isEdit) {
    activityRules.value = {}
  }
})

const showBannerModal = (b = null) => {
  if (b) {
    bannerModal.value.isEdit = true
    bannerModal.value.form = { ...b }
  } else {
    bannerModal.value.isEdit = false
    bannerModal.value.form = { imageUrl: '', position: 'HOME', sort: 0, linkType: 'NONE', linkValue: '' }
  }
  bannerModal.value.show = true
}

const handleBannerFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    // 使用封装好的 uploadFile 工具，它会自动处理 FormData 和 Content-Type
    const res = await uploadFile('/api/common/upload/image', file, { type: 'banner' })
    
    // 统一处理响应结构
    let url = ''
    if (res) {
      // uploadFile 内部使用的是 service 实例，拦截器会返回 res (包含 code, data 等)
      if (res.code === 200 && res.data && res.data.url) {
        url = res.data.url
      } else if (res.data && res.data.url) {
        url = res.data.url
      } else if (res.url) {
        url = res.url
      }
    }

    if (url) {
      bannerModal.value.form.imageUrl = url
    } else {
      throw new Error('未获取到图片URL')
    }
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败: ' + (error.message || '未知错误'))
  }
}

const saveBanner = async () => {
  try {
    if (bannerModal.value.isEdit) {
      await put(`/api/admin/banners/${bannerModal.value.form.id}`, bannerModal.value.form)
    } else {
      await post('/api/admin/banners', bannerModal.value.form)
    }
    bannerModal.value.show = false
    loadBanners()
    alert('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

const deleteBanner = async (b) => {
  if (confirm('确定要删除这张轮播图吗？')) {
    await del(`/api/admin/banners/${b.id}`)
    loadBanners()
  }
}

const showActivityModal = (a = null) => {
  if (a) {
    activityModal.value.isEdit = true
    // 确保时间格式符合 datetime-local 的要求 (YYYY-MM-DDTHH:mm)
    activityModal.value.form = {
      ...a,
      startTime: a.startTime ? a.startTime.replace(' ', 'T').substring(0, 16) : '',
      endTime: a.endTime ? a.endTime.replace(' ', 'T').substring(0, 16) : ''
    }
    try {
      activityRules.value = JSON.parse(a.rulesJson || '{}')
    } catch (e) {
      activityRules.value = {}
    }
  } else {
    activityModal.value.isEdit = false
    activityModal.value.form = { name: '', type: 'FULL_REDUCE' }
    activityRules.value = { threshold: 0, reduce: 0 }
  }
  activityModal.value.show = true
}

const saveActivity = async () => {
  // 创建提交数据的副本，避免修改原始表单导致界面显示异常
  const submitData = { ...activityModal.value.form }
  
  // 处理指定商品 ID 字符串转数组
  if (submitData.type === 'PROMOTION_DISCOUNT' && activityRules.value.productIdsStr) {
    activityRules.value.productIds = activityRules.value.productIdsStr.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))
  }

  // 将可视化规则转换回 JSON
  submitData.rulesJson = JSON.stringify(activityRules.value)
  // 确保时间格式符合后端要求 (YYYY-MM-DDTHH:mm:ss)
  submitData.startTime = formatDateTime(submitData.startTime)
  submitData.endTime = formatDateTime(submitData.endTime)
  
  try {
    if (activityModal.value.isEdit) {
      await put(`/api/admin/activities/${submitData.id}`, submitData)
    } else {
      await post('/api/admin/activities', submitData)
    }
    activityModal.value.show = false
    loadActivities()
    alert('保存成功')
  } catch (error) {
    console.error('保存活动失败:', error)
    alert('保存失败: ' + (error.message || '未知错误'))
  }
}

const loadActivities = async () => {
  // DEMO ONLY: 优先从 IndexedDB 获取促销活动
  const res = await promotionApi.getPromotionList()
  const resData = res.data || res
  activities.value = Array.isArray(resData) ? resData : (resData.list || [])
}

const loadCoupons = async () => {
  const res = await get('/api/admin/coupon-templates')
  couponTemplates.value = res.data
}

const loadBanners = async () => {
  const res = await get('/api/admin/banners')
  banners.value = res.data
}

const loadPushTasks = async () => {
  const res = await get('/api/admin/push/tasks')
  // 兼容 Page 结构和普通数组结构
  pushTasks.value = res.data.content || res.data || []
}

watch(activeTab, (val) => {
  if (val === 'activity') loadActivities()
  if (val === 'coupon') loadCoupons()
  if (val === 'banner') loadBanners()
  if (val === 'push') loadPushTasks()
})

const toggleActivity = async (a) => {
  await put(`/api/admin/activities/${a.id}/status`, { isActive: !a.isActive })
  loadActivities()
}

const viewEffect = async (a) => {
  const res = await get(`/api/admin/activities/${a.id}/analysis`)
  const data = res.data
  alert(`活动 ${data.name} 效果分析：\n参与人数：${data.participantCount}\n订单总数：${data.orderCount}\n优惠总额：¥${data.totalDiscountAmount}\n转化率：${data.conversionRate}`)
}

const showPushModal = () => {
  pushModal.value.form = { pushType: 'MARKETING', targetType: 'ALL', triggerType: 'IMMEDIATE' }
  pushModal.value.show = true
}

const savePushTask = async () => {
  await post('/api/admin/push/tasks', pushModal.value.form)
  pushModal.value.show = false
  loadPushTasks()
}

const viewPushStats = async (p) => {
  const res = await get(`/api/admin/push/tasks/${p.id}/statistics`)
  const data = res.data
  alert(`推送任务效果分析：\n发送人数：${data.sentCount}\n送达人数：${data.reachCount}\n阅读人数：${data.readCount}\n点击人数：${data.clickCount}`)
}

const distributeModal = ref({ show: false, template: null, members: [], selectedIds: [], loading: false })

const distributeCoupon = async (t) => {
  distributeModal.value.template = t
  distributeModal.value.selectedIds = []
  distributeModal.value.show = true
  loadMembersForDistribute()
}

const loadMembersForDistribute = async () => {
  distributeModal.value.loading = true
  try {
    const res = await get('/api/admin/members', { size: 100 })
    distributeModal.value.members = res.data.content || []
  } catch (error) {
    console.error('加载会员失败:', error)
  } finally {
    distributeModal.value.loading = false
  }
}

const toggleMemberSelection = (id) => {
  const index = distributeModal.value.selectedIds.indexOf(id)
  if (index > -1) {
    distributeModal.value.selectedIds.splice(index, 1)
  } else {
    distributeModal.value.selectedIds.push(id)
  }
}

const isAllSelected = ref(false)
const toggleAllMembers = () => {
  if (isAllSelected.value) {
    distributeModal.value.selectedIds = []
  } else {
    distributeModal.value.selectedIds = distributeModal.value.members.map(m => m.id)
  }
  isAllSelected.value = !isAllSelected.value
}

const confirmDistribute = async () => {
  if (distributeModal.value.selectedIds.length === 0) {
    alert('请至少选择一个用户')
    return
  }
  
  try {
    await post('/api/admin/coupons/distribute', {
      templateId: distributeModal.value.template.id,
      userIds: distributeModal.value.selectedIds
    })
    alert('发放成功')
    distributeModal.value.show = false
  } catch (error) {
    console.error('发放失败:', error)
    alert('发放失败: ' + (error.message || '未知错误'))
  }
}

const viewDistribution = async (t) => {
  const res = await get('/api/admin/coupons/distribution-records')
  const records = res.data.filter(r => r.couponTemplate.id === t.id)
  const count = records.length
  const usedCount = records.filter(r => r.used).length
  alert(`优惠券 [${t.name}] 发放记录：\n总计发放：${count} 张\n已使用：${usedCount} 张`)
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return null
  // 将 datetime-local 的格式 (YYYY-MM-DDTHH:mm) 转换为后端需要的 ISO 格式 (YYYY-MM-DDTHH:mm:ss)
  const date = new Date(dateTimeStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = '00'
  return `${y}-${m}-${d}T${h}:${min}:${s}`
}

onMounted(() => {
  loadActivities()
})
</script>
<style scoped>
.marketing-manage {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f0e1 0%, #f8f4e6 100%);
  min-height: 100vh;
}

.card {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  border-radius: 30px;
  padding: 28px;
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 3px solid #e8dccb;
  backdrop-filter: blur(6px);
  transition: all 0.3s ease-out;
}

.marketing-tabs {
  display: flex;
  gap: 32px;
  padding: 0 28px;
  height: 70px;
  align-items: center;
  border-bottom: 3px dashed #d4c7b5;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.8) 100%);
  border-radius: 30px 30px 0 0;
}

.tab-item {
  cursor: pointer;
  color: #7a6a5b;
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  padding: 0 12px;
  transition: all 0.3s ease-out;
  border-radius: 20px;
}

.tab-item:hover {
  color: #a0522d;
  background: rgba(255, 192, 203, 0.1);
  transform: translateY(-2px);
}

.tab-item.active {
  color: #8b4513;
  font-weight: 700;
  opacity: 1;
  background: linear-gradient(135deg, rgba(255, 192, 203, 0.2) 0%, rgba(210, 180, 140, 0.1) 100%);
}

.tab-item.active::after {
  content: '✨';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 18px;
  color: #ffc0cb;
}

.content-container {
  min-height: 600px;
  border-radius: 30px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 3px dashed #d4c7b5;
}

.section-header h3 {
  font-size: 22px;
  font-weight: 700;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  margin: 0;
}

.btn-success {
  background: linear-gradient(135deg, #2c9678 0%, #1e7d5e 100%);
  color: #fff8dc;
  border: none;
  padding: 14px 28px;
  border-radius: 25px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  box-shadow: 0 6px 20px rgba(44, 150, 120, 0.3);
  transition: all 0.3s ease-out;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-success::before {
  content: '➕';
  font-size: 18px;
}

.btn-success:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 25px rgba(44, 150, 120, 0.4);
}

.btn-success:active {
  transform: translateY(0) scale(0.98);
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.1);
}

.admin-table th {
  background: linear-gradient(135deg, #a0522d 0%, #8b4513 100%);
  color: #fff8dc;
  font-weight: 700;
  font-size: 16px;
  padding: 20px;
  text-align: left;
  letter-spacing: 0.02em;
  border-bottom: 3px solid #d2b48c;
}

.admin-table td {
  padding: 20px;
  color: #4a3b30;
  font-size: 15px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.7);
  border-bottom: 2px dashed #e8dccb;
  transition: all 0.3s ease-out;
}

.admin-table tr:hover td {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.7) 100%);
  transform: translateX(8px);
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.status-on {
  color: #2c9678;
  font-weight: 700;
  background: rgba(44, 150, 120, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  border: 2px solid rgba(44, 150, 120, 0.3);
  display: inline-block;
}
.status-off {
  color: #ff6b6b;
  font-weight: 700;
  background: rgba(255, 107, 107, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  border: 2px solid rgba(255, 107, 107, 0.3);
  display: inline-block;
}

.status-making {
  color: #f39c12;
  background: rgba(243, 156, 18, 0.1);
  padding: 4px 12px;
  border-radius: 15px;
  font-weight: bold;
}

.status-ready {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
  padding: 4px 12px;
  border-radius: 15px;
  font-weight: bold;
}

.status-completed {
  color: #7f8c8d;
  background: rgba(127, 140, 141, 0.1);
  padding: 4px 12px;
  border-radius: 15px;
  font-weight: bold;
}

.pickup-badge {
  background: #8b4513;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: 'Prompt', sans-serif;
  font-weight: bold;
}

.ops {
  display: flex;
  gap: 12px;
}

.ops button {
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1) 0%, rgba(139, 69, 19, 0.05) 100%);
  color: #8b4513;
  border: 2px solid rgba(160, 82, 45, 0.3);
  padding: 8px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.02em;
  transition: all 0.3s ease-out;
}

.ops button:hover {
  background: linear-gradient(135deg, #ffc0cb 0%, #a0522d 100%);
  color: #fff8dc;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 15px rgba(160, 82, 45, 0.3);
  border-color: #a0522d;
}

.banner-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 28px;
}

.banner-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 244, 230, 0.8) 100%);
  border-radius: 25px;
  overflow: hidden;
  border: 3px solid #e8dccb;
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.banner-card:hover {
  transform: translateY(-8px) scale(1.03);
  box-shadow: 0 15px 35px rgba(160, 82, 45, 0.2);
  border-color: #d2b48c;
}

.banner-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  filter: saturate(1.1) contrast(1.05);
  border-bottom: 3px dashed #d4c7b5;
}

.banner-info {
  padding: 20px;
  font-size: 15px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  line-height: 1.6;
}

.banner-ops {
  padding: 20px;
  border-top: 3px dashed #d4c7b5;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  background: rgba(255, 248, 220, 0.5);
}

.banner-ops button {
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1) 0%, rgba(139, 69, 19, 0.05) 100%);
  color: #8b4513;
  border: 2px solid rgba(160, 82, 45, 0.3);
  padding: 10px 24px;
  border-radius: 20px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.02em;
  transition: all 0.3s ease-out;
}

.banner-ops button:hover {
  background: linear-gradient(135deg, #ffc0cb 0%, #a0522d 100%);
  color: #fff8dc;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 15px rgba(160, 82, 45, 0.3);
  border-color: #a0522d;
}

.text-danger {
  color: #ff6b6b !important;
  font-weight: 700;
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload-preview {
  width: 100%;
  height: 150px;
  border: 2px dashed #d4c7b5;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.upload-preview:hover {
  border-color: #a0522d;
  background: rgba(255, 248, 220, 0.8);
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  text-align: center;
  color: #a09080;
}

.upload-placeholder span {
  font-size: 40px;
  display: block;
  margin-bottom: 5px;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(139, 69, 19, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);

.coupon-preview-box {
  margin-top: 20px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 15px;
  border: 2px dashed #d4c7b5;
}

.coupon-card-preview {
  display: flex;
  width: 100%;
  height: 100px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-top: 10px;
  position: relative;
}

.coupon-card-preview::before {
  content: '';
  position: absolute;
  left: 28%;
  top: -10px;
  bottom: -10px;
  width: 20px;
  background-image: radial-gradient(circle at 10px 10px, transparent 10px, #fff 10px);
  background-size: 20px 20px;
  z-index: 1;
}

.coupon-left {
  width: 30%;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  position: relative;
}

.coupon-card-preview.DISCOUNT .coupon-left {
  background: linear-gradient(135deg, #4dabf7 0%, #74c0fc 100%);
}

.coupon-value {
  font-size: 24px;
  font-weight: bold;
}

.coupon-value .num {
  font-size: 32px;
}

.coupon-threshold {
  font-size: 12px;
  opacity: 0.9;
}

.coupon-right {
  flex: 1;
  padding: 15px 15px 15px 25px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #fff;
}

.coupon-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.coupon-time {
  font-size: 12px;
  color: #999;
}

.form-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.form-scroll-container::-webkit-scrollbar-thumb {
  background-color: #d4c7b5;
  border-radius: 3px;
}

}

.modal-container {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  padding: 32px;
  border-radius: 30px;
  width: 500px;
  max-width: 90vw;
  box-shadow: 0 15px 40px rgba(160, 82, 45, 0.25);
  border: 3px solid #d2b48c;
  animation: modalAppear 0.4s ease-out;
}

@keyframes modalAppear {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-container h3 {
  font-size: 24px;
  font-weight: 700;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 3px dashed #d4c7b5;
  text-align: center;
}

.form-item {
  margin-bottom: 24px;
}

.form-item label {
  display: block;
  margin-bottom: 12px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.02em;
}

.admin-input, .admin-select {
  width: 100%;
  padding: 14px 20px;
  border: 3px solid #e8dccb;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.9);
  color: #4a3b30;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease-out;
  outline: none;
}

.admin-input:focus, .admin-select:focus {
  border-color: #a0522d;
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
  transform: translateY(-2px);
}

.admin-input::placeholder {
  color: #a09080;
  opacity: 0.7;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 3px dashed #d4c7b5;
}

.modal-footer button {
  padding: 12px 28px;
  border-radius: 25px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  transition: all 0.3s ease-out;
  border: 3px solid;
}

.modal-footer button:first-child {
  background: rgba(255, 255, 255, 0.9);
  color: #7a6a5b;
  border-color: #d4c7b5;
}

.modal-footer button:first-child:hover {
  background: rgba(255, 192, 203, 0.2);
  color: #a0522d;
  border-color: #ffc0cb;
  transform: translateY(-2px);
}

.btn-primary {
  background: linear-gradient(135deg, #a0522d 0%, #8b4513 100%);
  color: #fff8dc;
  border-color: #d2b48c !important;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #8b4513 0%, #a0522d 100%);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.4);
}

.btn-primary:active {
  transform: translateY(0) scale(0.98);
}
</style>