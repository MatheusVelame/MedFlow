package br.com.medflow.dominio.convenios;

public enum AcaoHistorico {
	CRIACAO,
	ATUALIZACAO,
	ARQUIVAMENTO,
	EXCLUSAO, // Ação sugerida para o futuro
	REVISAO_SOLICITADA,
	REVISAO_APROVADA,
	REVISAO_REPROVADA
}