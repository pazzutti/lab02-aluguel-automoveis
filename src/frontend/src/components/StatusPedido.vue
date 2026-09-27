<script setup>
import { computed } from 'vue'
import { SITUACOES } from '@/services/pedidoApi'

const props = defineProps({
  situacao: { type: String, required: true },
})

const rotulo = computed(() => SITUACOES[props.situacao]?.rotulo ?? props.situacao)
</script>

<template>
  <span class="selo" :class="situacao.toLowerCase()">{{ rotulo }}</span>
</template>

<style scoped>
.selo {
  display: inline-block;
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: bold;
  white-space: nowrap;
}

.nao_avaliado {
  background: hsla(40, 100%, 50%, 0.18);
  color: hsl(35, 90%, 35%);
}

.em_analise {
  background: hsla(210, 100%, 50%, 0.15);
  color: hsl(210, 80%, 40%);
}

.avaliado {
  background: hsla(270, 70%, 55%, 0.15);
  color: hsl(270, 50%, 45%);
}

.aceito {
  background: hsla(160, 100%, 37%, 0.15);
  color: hsl(160, 100%, 25%);
}

.recusado {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
}

.cancelado {
  background: var(--color-background-mute);
  color: var(--color-text);
}
</style>
