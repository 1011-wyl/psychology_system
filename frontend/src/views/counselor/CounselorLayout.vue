<template>
  <PortalShell role="counselor"><router-view /></PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
export default { name: 'CounselorLayout', components: { PortalShell } }
</script>
