package br.com.medflow.apresentacao.financeiro.convenios;

import br.com.medflow.aplicacao.financeiro.convenios.ConvenioServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioResumo;
import br.com.medflow.dominio.financeiro.convenios.ConvenioServico;
import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import br.com.medflow.dominio.financeiro.convenios.ConvenioId;
import br.com.medflow.dominio.financeiro.convenios.UsuarioResponsavelId;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/backend/convenios")
public class ConvenioControlador {

	private final ConvenioServicoAplicacao servicoConsulta;
	private final ConvenioServico servicoDominio;

	public ConvenioControlador(
			ConvenioServicoAplicacao servicoConsulta,
			ConvenioServico servicoDominio) {
		this.servicoConsulta = servicoConsulta;
		this.servicoDominio = servicoDominio;
	}

	// =====================================================================
	// QUERIES (Leitura)
	// =====================================================================

	// GET /backend/convenios
	@GetMapping
	public List<ConvenioResumo> pesquisarConvenios() {
		return servicoConsulta.pesquisarResumos();
	}

	// GET /backend/convenios/{id}
	@GetMapping("/{id}")
	public ResponseEntity<?> obterDetalhes(@PathVariable Integer id) {
		try {
			var detalhes = servicoConsulta.obterDetalhes(id);
			return ResponseEntity.ok(detalhes);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// GET /backend/convenios/codigo/{codigoIdentificacao}
	@GetMapping("/codigo/{codigoIdentificacao}")
	public List<ConvenioResumo> pesquisarPorCodigo(@PathVariable String codigoIdentificacao) {
		return servicoConsulta.pesquisarPorCodigoIdentificacao(codigoIdentificacao);
	}

	// GET /backend/convenios/status/{status}
	@GetMapping("/status/{status}")
	public List<ConvenioResumo> pesquisarPorStatus(@PathVariable StatusConvenio status) {
		return servicoConsulta.pesquisarPorStatus(status);
	}

	// =====================================================================
	// COMMANDS (Escrita/Ação) - MÉTODOS DO CONVENIOSERVICO
	// =====================================================================

	// 1. Comando: cadastrar
	// POST /backend/convenios
	@Transactional
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void cadastrarConvenio(@Valid @RequestBody ConvenioFormulario formulario) {
		var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());

		servicoDominio.cadastrar(
			formulario.getNome(),
			formulario.getCodigoIdentificacao(),
			responsavelId
		);
	}

	// 2. Comando: alterarNome
	@Transactional
	@PatchMapping("/{id}/nome")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void alterarNome(@PathVariable Integer id, @Valid @RequestBody NomeFormulario formulario) {
		var convenioId = new ConvenioId(id);
		var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());

		servicoDominio.alterarNome(
			convenioId,
			formulario.getNovoNome(),
			responsavelId
		);
	}

	// 3. Comando: mudarStatus
	@Transactional
	@PutMapping("/{id}/status/{novoStatus}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void mudarStatus(
			@PathVariable Integer id,
			@PathVariable StatusConvenio novoStatus,
			@Valid @RequestBody UsuarioResponsavelFormulario responsavel,
			@RequestParam(defaultValue = "false") boolean temProcedimentoAtivo) {

		var convenioId = new ConvenioId(id);
		var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());

		servicoDominio.mudarStatus(
			convenioId,
			novoStatus,
			responsavelId,
			temProcedimentoAtivo
		);
	}

	// 4. Comando: excluir
	@Transactional
	@DeleteMapping("/{codigoIdentificacao}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirConvenio(
			@PathVariable String codigoIdentificacao,
			@Valid @RequestBody UsuarioResponsavelFormulario responsavel,
			@RequestParam(defaultValue = "false") boolean temProcedimentoAtivo) {

		var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());

		servicoDominio.excluir(
			codigoIdentificacao,
			responsavelId,
			temProcedimentoAtivo
		);
	}
}

