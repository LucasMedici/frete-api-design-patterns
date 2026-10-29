package br.com.freteapi.exception;

public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}
