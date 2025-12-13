import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para normalizar erros do backend em mensagens amigáveis
api.interceptors.response.use(
  (resp) => resp,
  (error) => {
    // Extrai mensagem do body (padrão do backend) ou usa mensagem do axios
    const data = error?.response?.data || {};
    const serverMessage = data?.message || data?.erro || null;
    const serverCode = data?.code || data?.errorCode || null;
    const status = error?.response?.status;

    // Mapear mensagens específicas para versões amigáveis
    let mapped = serverMessage;
    if (serverCode && typeof serverCode === "string") {
      const codeLower = serverCode.toLowerCase();
      // Exemplos de códigos que o backend pode retornar
      if (
        codeLower.includes("duplicate") ||
        codeLower.includes("conflit") ||
        codeLower.includes("exame_duplicado") ||
        codeLower.includes("duplicado")
      ) {
        mapped = "Já existe um exame agendado para este paciente neste horário.";
      }
    }

    if (!mapped && mapped !== "") {
      if (mapped && typeof mapped === "string") {
        const lower = mapped.toLowerCase();
        // Caso comum: conflito de agendamento para mesmo paciente/horário
        if (
          (lower.includes("mesmo") && lower.includes("hor")) ||
          lower.includes("mesmo horário") ||
          lower.includes("já existe") ||
          lower.includes("conflit") ||
          lower.includes("duplicate")
        ) {
          mapped = "Já existe um exame agendado para este paciente neste horário.";
        }
        // outros mapeamentos possíveis podem ser adicionados aqui
      }
    }

    const msg =
      mapped || (status ? `Erro ${status}: ${error.message}` : error.message) || "Erro de rede";
    return Promise.reject(new Error(msg));
  }
);

// helper to extract data and handle typed responses
export async function request<T>(promise: Promise<any>): Promise<T> {
  try {
    const resp = await promise;
    return resp.data as T;
  } catch (err: any) {
    // Se já for Error, rethrow para manter a mensagem normalizada
    if (err instanceof Error) throw err;
    // fallback
    throw new Error(err?.message ?? "Erro desconhecido");
  }
}