# Welcome to your Lovable project

## Project info

**URL**: https://lovable.dev/projects/83b3df5b-93cc-497f-9b20-9732f14e1075

## How can I edit this code?

There are several ways of editing your application.

**Use Lovable**

Simply visit the [Lovable Project](https://lovable.dev/projects/83b3df5b-93cc-497f-9b20-9732f14e1075) and start prompting.

Changes made via Lovable will be committed automatically to this repo.

**Use your preferred IDE**

If you want to work locally using your own IDE, you can clone this repo and push changes. Pushed changes will also be reflected in Lovable.

The only requirement is having Node.js & npm installed - [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating)

Follow these steps:

```sh
# Step 1: Clone the repository using the project's Git URL.
git clone <YOUR_GIT_URL>

# Step 2: Navigate to the project directory.
cd <YOUR_PROJECT_NAME>

# Step 3: Install the necessary dependencies.
npm i

# Step 4: Start the development server with auto-reloading and an instant preview.
npm run dev
```

## Local development with backend on localhost:8080

If your backend runs on http://localhost:8080 and the frontend on a different port (example: 8090), set the API base URL using an env file. Copy the example file and edit if needed:

```bash
cp .env.local.example .env.local
# then (Windows PowerShell)
$env:PORT=8090
npm run dev -- --port 8090
```

The project reads `VITE_API_BASE_URL` in `src/api/apiClient.ts`.

If you see the Spring Whitelabel Error Page at http://localhost:8080/, that's normal when the root path has no mapping. Test a real API path to validate the API, for example:

```bash
curl http://localhost:8080/backend/tipos-exames
```

If the API returns JSON, the backend is working even if `/` shows the Whitelabel 404.

### CORS
When frontend and backend run on different origins (different ports count as different origins), the browser enforces CORS. If you get CORS errors in the browser console, add one of the following to the Spring backend:

Option A — per controller (quick):

```java
@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/backend/exames")
public class ExameController { ... }
```

Option B — global config (recommended for dev):

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
						.allowedOrigins("http://localhost:8090")
						.allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
						.allowCredentials(true);
	}
}
```

### Mapping backend validation errors into forms
When the backend returns HTTP 400 or 409 with a payload containing validation errors, it's good UX to show those errors next to the corresponding input. The frontend uses `react-hook-form`; implement something like this in your mutation `onError` handler:

```ts
// exemplo simplificado dentro de um onError
onError: (error) => {
	const payload = error.response?.data;
	// payload.errors expected as { fieldName: "mensagem" }
	if (payload?.errors) {
		Object.entries(payload.errors).forEach(([field, message]) => {
			form.setError(field as any, { type: 'server', message: String(message) });
		});
		return;
	}
	// fallback: toast generic
	toast.error(payload?.message || 'Erro ao processar a requisição');
}
```

Add this pattern to the `onError` of mutations used by `ExameForm` and `EspecialidadeForm` to show server-side validation messages inline.


**Edit a file directly in GitHub**

- Navigate to the desired file(s).
- Click the "Edit" button (pencil icon) at the top right of the file view.
- Make your changes and commit the changes.

**Use GitHub Codespaces**

- Navigate to the main page of your repository.
- Click on the "Code" button (green button) near the top right.
- Select the "Codespaces" tab.
- Click on "New codespace" to launch a new Codespace environment.
- Edit files directly within the Codespace and commit and push your changes once you're done.

## What technologies are used for this project?

This project is built with:

- Vite
- TypeScript
- React
- shadcn-ui
- Tailwind CSS

## How can I deploy this project?

Simply open [Lovable](https://lovable.dev/projects/83b3df5b-93cc-497f-9b20-9732f14e1075) and click on Share -> Publish.

## Can I connect a custom domain to my Lovable project?

Yes, you can!

To connect a domain, navigate to Project > Settings > Domains and click Connect Domain.

Read more here: [Setting up a custom domain](https://docs.lovable.dev/tips-tricks/custom-domain#step-by-step-guide)
