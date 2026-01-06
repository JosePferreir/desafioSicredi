CREATE TABLE pauta (
                       id BIGSERIAL PRIMARY KEY,
                       titulo VARCHAR(255) NOT NULL,
                       descricao VARCHAR(500),
                       resultado VARCHAR(20)
);

CREATE TABLE votacao (
                                id BIGSERIAL PRIMARY KEY,
                                pauta_id BIGINT NOT NULL UNIQUE,
                                data_inicio TIMESTAMP NOT NULL,
                                data_fim TIMESTAMP NOT NULL,
                                CONSTRAINT fk_votacao_pauta FOREIGN KEY (pauta_id) REFERENCES pauta(id)
);

CREATE TABLE associado (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(255) NOT NULL,
                           cpf VARCHAR(11) NOT NULL UNIQUE
);

CREATE TABLE voto (
                      id BIGSERIAL PRIMARY KEY,
                      votacao_id BIGINT NOT NULL,
                      associado_id BIGINT NOT NULL,
                      valor VARCHAR(10) NOT NULL,
                      CONSTRAINT fk_voto_votacao
                          FOREIGN KEY (votacao_id) REFERENCES votacao(id),
                      CONSTRAINT fk_voto_associado
                          FOREIGN KEY (associado_id) REFERENCES associado(id),
                      CONSTRAINT fk_voto_sessao_associado
                          UNIQUE (votacao_id, associado_id)
);