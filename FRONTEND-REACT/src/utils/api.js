export const fetchApi = async (url, options = {}) => {
    // Determine the base URL
    const baseUrl = 'http://localhost:8080/api';
    const fullUrl = url.startsWith('http') ? url : `${baseUrl}${url.startsWith('/') ? '' : '/'}${url}`;

    // Get the token from local storage
    const token = localStorage.getItem('token');

    // Merge custom headers with standard headers
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    // If we have a token, add the Authorization header
    // But don't override an existing auth header if one was provided in options
    if (token && !headers['Authorization']) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // For FormData or Blob uploads, we might not want JSON content type
    if (options.body instanceof FormData) {
        delete headers['Content-Type'];
    }

    const fetchOptions = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(fullUrl, fetchOptions);

        // Intercept 401 Unauthorized errors
        if (response.status === 401 || response.status === 403) {
            // Only trigger logout if it's not the actual auth endpoint
            if (!fullUrl.includes('/auth/')) {
                console.warn('Unauthorized request intercepted. Logging user out.');
                localStorage.removeItem('token');
                // Force a page reload to let App.jsx handle the redirect back to Login
                window.location.reload();
            }
        }

        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
};
