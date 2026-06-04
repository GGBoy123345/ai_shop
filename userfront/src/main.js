import { createApp } from 'vue'
import { createPinia } from 'pinia'
import vant, { Lazyload } from 'vant'
import router from './router'
import App from './App.vue'
import 'vant/lib/index.css'
import './styles/global.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(vant)
app.use(Lazyload)
app.mount('#app')
