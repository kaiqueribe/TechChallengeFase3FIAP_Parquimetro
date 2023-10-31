package br.com.fiap.parquimetro.controller;

import br.com.fiap.parquimetro.model.Condutor;
import br.com.fiap.parquimetro.model.Veiculo;
import br.com.fiap.parquimetro.repository.VeiculoRepository;
import br.com.fiap.parquimetro.service.CondutorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/condutores")
@AllArgsConstructor
public class CondutorController {
    private final CondutorService condutorService;
    private final VeiculoRepository veiculoRepository;

    @PostMapping()
    public ResponseEntity<Condutor> registrarCondutor(@RequestBody Condutor condutor) {
        Condutor novoCondutor = condutorService.registrarCondutor(condutor);
        return new ResponseEntity<>(novoCondutor, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Condutor>> listarTodosOsCondutores() {
        List<Condutor> condutores = condutorService.listarTodosOsCondutores();
        return new ResponseEntity<>(condutores, HttpStatus.OK);
    }

    @GetMapping("/{condutorId}")
    public ResponseEntity<Condutor> obterCondutor(@PathVariable String condutorId) {
        Optional<Condutor> condutor = condutorService.obterCondutorPorId(condutorId);
        return condutor.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{condutorId}/veiculos")
    public ResponseEntity<String> vincularVeiculoAoCondutor(@PathVariable String condutorId, @RequestParam String veiculoId) {
        Optional<Condutor> condutorOptional = condutorService.obterCondutorPorId(condutorId);
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(veiculoId);

        if (condutorOptional.isPresent() && veiculoOptional.isPresent()) {
            Condutor condutor = condutorOptional.get();
            Veiculo veiculo = veiculoOptional.get();

            // Verifique se a lista de veículos não é nula e inicialize-a se for.
            if (condutor.getVeiculos() == null) {
                condutor.setVeiculos(new ArrayList<>());
            }

            condutor.getVeiculos().add(veiculo);
            condutorService.atualizarCondutor(condutor);
            return new ResponseEntity<>("Veículo vinculado ao condutor com sucesso.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Condutor ou Veículo não encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{condutorId}/veiculos")
    public ResponseEntity<List<Veiculo>> obterVeiculosDoCondutor(@PathVariable String condutorId) {
        Optional<Condutor> condutor = condutorService.obterCondutorPorId(condutorId);
        if (condutor.isPresent()) {
            return new ResponseEntity<>(condutor.get().getVeiculos(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping()
    public ResponseEntity<Void> deletarTodosOsCondutores() {
        condutorService.deletarTodosOsCondutores();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}