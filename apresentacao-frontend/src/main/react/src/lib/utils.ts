import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// NOVA FUNÇÃO: Formatação segura de data/hora
export function formatDataHora(dateString: string | undefined | null): string {
    if (!dateString) {
        return "N/A";
    }
    try {
        const date = new Date(dateString);
        // Verifica se a data é válida (retorna true se for um número, false se for NaN)
        if (isNaN(date.getTime())) {
            return "Data Inválida";
        }
        // Formata a data e hora para o padrão brasileiro
        return date.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
    } catch (e) {
        // Em caso de erro de parsing
        console.error("Erro ao formatar data:", e);
        return "Erro de Formato";
    }
}