import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// helper to extract data and handle typed responses
export async function request<T>(promise: Promise<any>): Promise<T> {
  const resp = await promise;
  return resp.data as T;
}
