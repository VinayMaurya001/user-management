package user.management.profile.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import user.management.profile.constant.Constants;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private JwtTokenProvider jwtTokenProvider;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		super(authenticationManager);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Authentication authentication = jwtTokenProvider.getAuthentication(request);
		
		if (authentication != null && jwtTokenProvider.validateToken(request)) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		if (request.getRequestURI().equals(Constants.LOGOUT_API)) {
			logger.info("logout request");
			String token = jwtTokenProvider.resolveToken(request);
			if (token != null) {
				JwtTokenProvider.loggedTokenList.remove(token);
				return;
			}
		}
		chain.doFilter(request, response);
	}

}
