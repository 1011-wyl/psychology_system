<template>
  <PortalShell role="admin"><router-view /></PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
export default { name: 'AdminLayout', components: { PortalShell } }
</script>
