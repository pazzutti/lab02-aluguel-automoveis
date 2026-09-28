<script setup>
import { computed } from 'vue'
import { SITUACOES_CONTRATO } from '@/services/contratoApi'

const props = defineProps({
  situacao: { type: String, required: true },
})

const rotulo = computed(() => SITUACOES_CONTRATO[props.situacao]?.rotulo ?? props.situacao)
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

.pendente {
  background: hsla(40, 100%, 50%, 0.18);
  color: hsl(35, 90%, 35%);
}

.em_execucao {
  background: hsla(160, 100%, 37%, 0.15);
  color: hsl(160, 100%, 25%);
}

.encerrado {
  background: var(--color-background-mute);
  color: var(--color-text);
}
</style>
