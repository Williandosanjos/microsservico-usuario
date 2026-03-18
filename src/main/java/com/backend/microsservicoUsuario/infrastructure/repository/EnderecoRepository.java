package com.backend.microsservicoUsuario.infrastructure.repository;

import com.backend.microsservicoUsuario.infrastructure.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
