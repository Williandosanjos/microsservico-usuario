package com.backend.microsservicoUsuario.business;

import com.backend.microsservicoUsuario.business.converter.UsuarioConverter;
import com.backend.microsservicoUsuario.business.dto.EnderecoDTO;
import com.backend.microsservicoUsuario.business.dto.TelefoneDTO;
import com.backend.microsservicoUsuario.business.dto.UsuarioDTO;
import com.backend.microsservicoUsuario.infrastructure.entity.Endereco;
import com.backend.microsservicoUsuario.infrastructure.entity.Telefone;
import com.backend.microsservicoUsuario.infrastructure.entity.Usuario;
import com.backend.microsservicoUsuario.infrastructure.exceptions.ConflictException;
import com.backend.microsservicoUsuario.infrastructure.exceptions.ResourceNotFoundException;
import com.backend.microsservicoUsuario.infrastructure.repository.EnderecoRepository;
import com.backend.microsservicoUsuario.infrastructure.repository.TelefoneRepository;
import com.backend.microsservicoUsuario.infrastructure.repository.UsuarioRepository;
import com.backend.microsservicoUsuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExixstente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado " + e.getMessage());
        }
    }

    public boolean verificaEmailExixstente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO BuscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Email não encontrado " + email)));
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        //Aqui Busca o email do usuário logado atraves do token (tirando a obrigação do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografa a senha caso a nova senha não esteja criptografada
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Aqui Busca os dados do usuário no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("email não localizado" + email));

        //Mescla os dados recebidos na requisição DTO com os dadosexistentes no banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //Salva os dados do Usuario já convertido e depois pegou o retorno e converteu para usuario DTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO ) {

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Id do endereço não encontrado" + idEndereco));

        Endereco enderecoEntity = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(enderecoEntity));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id do Telefone não encontrado" +  idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoDTO cadastrarEndereco(String token, EnderecoDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("email não localizado " + email));

        Endereco endereco = usuarioConverter.paraEndereçoEntity(dto, usuario.getId());

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO cadastrarTelefone(String token, TelefoneDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("email não localizado " + email));

        Telefone telefone = usuarioConverter.paraTelefone(dto, usuario.getId());

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }
}
