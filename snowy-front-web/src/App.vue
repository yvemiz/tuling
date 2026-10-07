<template>
  <a-config-provider
    :theme="{
      token: {
        colorPrimary: '#3b82f6',
        borderRadius: 8,
        fontFamily: `'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`
      }
    }"
  >
    <router-view />
  </a-config-provider>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchProfile()
  }
})
</script>

<style>
#app {
  min-height: 100vh;
}
</style>
