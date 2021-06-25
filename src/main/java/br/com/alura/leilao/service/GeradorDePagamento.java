package br.com.alura.leilao.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Pagamento;

@Service
public class GeradorDePagamento {

	@Autowired
	private PagamentoDao pagamentos;

	public void gerarPagamento(Lance lanceVencedor) {
		LocalDate vencimento = LocalDate.now().plusDays(1);
		Pagamento pagamento = new Pagamento(lanceVencedor, vencimento);
		this.pagamentos.salvar(pagamento);
	}

}
