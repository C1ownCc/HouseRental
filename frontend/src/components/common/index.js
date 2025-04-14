import Loading from './Loading.vue'
import Empty from './Empty.vue'
import ImageUpload from './ImageUpload.vue'

export default {
  install(app) {
    app.component('AppLoading', Loading)
    app.component('AppEmpty', Empty)
    app.component('AppImageUpload', ImageUpload)
  }
} 