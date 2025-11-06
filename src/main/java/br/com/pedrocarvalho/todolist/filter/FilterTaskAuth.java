package br.com.pedrocarvalho.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.pedrocarvalho.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importação que estava faltando na primeira imagem (para NonNull), 
// mas não é obrigatória se você não estiver usando um compilador estrito.
// Se precisar, adicione a dependência `spring-jcl` ou similar.
// import org.springframework.lang.NonNull; 

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        // 1. **VERIFICAÇÃO DE ROTA (WHITELIST)**
        // Se a rota for de criação de usuário (/users/), libera a requisição imediatamente
        // (ela não exige autenticação).
        if (servletPath.startsWith("/users/")) {
            filterChain.doFilter(request, response);
            return; // Termina o processamento do filtro aqui.
        }

        // 2. **PROTEÇÃO DE ROTA (BASIC AUTH)**
        // Se a rota NÃO for /users/ (por exemplo, /tasks/), exige autenticação.
        
        // a) Pegar Autenticação (usuário e senha) do cabeçalho
        var authorization = request.getHeader("Authorization");

        // Verifica se o cabeçalho Authorization existe
        if (authorization == null || !authorization.startsWith("Basic")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sem autorização ou formato de autenticação inválido.");
            return;
        }

        // b) Decodificar a string Basic Auth
        var authEncoded = authorization.substring("Basic".length()).trim();
        byte[] authDecode = Base64.getDecoder().decode(authEncoded);
        var authString = new String(authDecode);

        String[] credentials = authString.split(":");
        if (credentials.length != 2) {
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Formato de credenciais inválido.");
             return;
        }
        String username = credentials[0];
        String password = credentials[1];

        // c) VALIDAR USUÁRIO
        var user = this.userRepository.findByUsername(username);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado.");
            return;
        }
        
        // d) VALIDAR A SENHA
        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        
        if (passwordVerify.verified) {
            // Se a autenticação for bem-sucedida, você pode configurar o ID do usuário na requisição
            // para ser usado no Controller (ex: Request Attribute)
            request.setAttribute("idUser", user.getId());
            
            // Permite que a requisição siga para o Controller
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário ou senha inválidos.");
        }
    }
}