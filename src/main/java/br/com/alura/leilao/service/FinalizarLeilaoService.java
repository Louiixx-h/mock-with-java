package br.com.alura.leilao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;

@Service
public class FinalizarLeilaoService {

	@Autowired
	private LeilaoDao leiloes;

	public void finalizarLeiloesExpirados() {
		List<Leilao> expirados = leiloes.buscarLeiloesExpirados();
		expirados.forEach(leilao -> {
			Lance maiorLance = maiorLanceDadoNoLeilao(leilao);
			leilao.setLanceVencedor(maiorLance);
			leilao.fechar();
			leiloes.salvar(leilao);
		});
	}

	private Lance maiorLanceDadoNoLeilao(Leilao leilao) {
		List<Lance> lancesDoLeilao = leilao.getLances();
		lancesDoLeilao.sort((lance1, lance2) -> {
			return lance2.getValor().compareTo(lance1.getValor());
		});
		return lancesDoLeilao.get(0);
	}
	
}
