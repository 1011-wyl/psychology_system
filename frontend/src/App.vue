<template>
  <div id="app" :class="{ 'portal-theme': usesPortalTheme }">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    usesPortalTheme() {
      return !['/login', '/counselor/login', '/admin/login'].includes(
        this.$route.path
      )
    },
  },
  watch: {
    usesPortalTheme: {
      immediate: true,
      handler(enabled) {
        document.body.classList.toggle('portal-ui', enabled)
      },
    },
  },
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}
</style>
