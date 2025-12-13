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
    const serverMessage = error?.response?.data?.message || error?.response?.data?.erro || null;
    const status = error?.response?.status;
    const msg = serverMessage || (status ? `Erro ${status}: ${error.message}` : error.message) || "Erro de rede";
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