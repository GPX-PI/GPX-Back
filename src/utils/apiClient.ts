// Utilitario para realizar peticiones HTTP autenticadas

const API_BASE_URL = typeof window !== 'undefined' 
  ? (window as any).location?.origin.includes('localhost') 
    ? 'http://localhost:8080' 
    : 'https://gpx-back.onrender.com'
  : 'https://gpx-back.onrender.com';

/**
 * Realiza una petición HTTP autenticada con JWT
 */
export async function authFetch(
  url: string,
  options: RequestInit = {}
): Promise<Response> {
  // Obtener token del localStorage
  const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;

  // Configurar headers por defecto
  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
  };

  // Agregar token si existe
  if (token) {
    defaultHeaders.Authorization = `Bearer ${token}`;
  }

  // Combinar headers
  const headers = {
    ...defaultHeaders,
    ...options.headers,
  };

  // Construir URL completa
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;

  // Realizar petición
  const response = await fetch(fullUrl, {
    ...options,
    headers,
  });

  // Si el token es inválido, redirigir a login
  if (response.status === 401) {
    // Limpiar token inválido
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    
    // Redirigir a login solo si no estamos ya en la página de login
    if (typeof window !== 'undefined' && !window.location.pathname.includes('/login')) {
      window.location.href = '/login';
    }
  }

  return response;
}

/**
 * Helper para hacer peticiones GET autenticadas
 */
export async function authGet(url: string): Promise<Response> {
  return authFetch(url, { method: 'GET' });
}

/**
 * Helper para hacer peticiones POST autenticadas
 */
export async function authPost(url: string, data?: any): Promise<Response> {
  return authFetch(url, {
    method: 'POST',
    body: data ? JSON.stringify(data) : undefined,
  });
}

/**
 * Helper para hacer peticiones PUT autenticadas
 */
export async function authPut(url: string, data?: any): Promise<Response> {
  return authFetch(url, {
    method: 'PUT',
    body: data ? JSON.stringify(data) : undefined,
  });
}

/**
 * Helper para hacer peticiones DELETE autenticadas
 */
export async function authDelete(url: string): Promise<Response> {
  return authFetch(url, { method: 'DELETE' });
} 