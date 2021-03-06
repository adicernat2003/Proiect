package com.example.licentaBackendSB.security;

import com.example.licentaBackendSB.loaders.StudentAccountsLoader;
import com.example.licentaBackendSB.loaders.StudentsLoader;
import com.example.licentaBackendSB.model.entities.StudentAccount;
import com.example.licentaBackendSB.repositories.StudentAccountRepository;
import com.example.licentaBackendSB.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.licentaBackendSB.loaders.StudentAccountsLoader.studentAccountsDB;
import static com.example.licentaBackendSB.loaders.StudentsLoader.studentsDB;
import static com.example.licentaBackendSB.security.UserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final StudentAccountRepository studentAccountRepository;
    private final StudentRepository studentRepository;
    private final StudentsLoader studentsLoader;
    private final StudentAccountsLoader studentAccountsLoader;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //configuram CSRF
                .csrf().disable()
                .authorizeRequests()    //vrem sa autorizam requesturi

                //Asta e pagina default la care are ORICINE nelogat acces: http://localhost:8080/
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()

                .anyRequest()           //orice request venit
                .authenticated()        //trebuie OBLIGATORIU sa fie autentificat

                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .passwordParameter("password")      //asta tre sa dea match cu "name" din login.html
                .usernameParameter("username")
                .defaultSuccessUrl("/menu", true)


                .and()
                .rememberMe()
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))    //defaults to 2 weeks
                .key("somethingVerySecured")   //cheia de criptare pt sessionId si expiration date, deobicei e md5
                .rememberMeParameter("remember-me")

                .and()

                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))      //doar fiindca CSRF e disabled, daca va fi enabled, musai tre sa fie POST
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login");
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {

        UserDetails userUser = User.builder()
                .username("checu")
                .password(passwordEncoder.encode("1233"))
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails adminUser = User.builder()
                .username("Adrian")
                .password(passwordEncoder.encode("1233"))
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails assistantUser = User.builder()
                .username("lixi")
                .password(passwordEncoder.encode("1233"))
                .authorities(ASSISTANT.getGrantedAuthorities())
                .build();

        List<UserDetails> accounts = new ArrayList<>(List.of(userUser, adminUser, assistantUser));

        studentsDB = studentRepository.findAll();
        studentAccountsDB = studentAccountRepository.findAll();

        if (studentsDB.isEmpty()) {
            studentsDB = studentsLoader.hardcodeStudents();
        }

        if (studentAccountsDB.isEmpty()) {
            studentAccountsDB = studentAccountsLoader.hardcodeStudentsAccountsDB(studentsDB);
        }

        for (StudentAccount studentAccount : studentAccountsDB) {
            if (Boolean.TRUE.equals(studentAccount.getIsActive())) {
                accounts.add(User.builder()
                        .username(studentAccount.getUsername())
                        .password(passwordEncoder.encode(studentAccount.getPassword()))
                        .authorities(STUDENT.getGrantedAuthorities())
                        .build());
            }
        }

        return new InMemoryUserDetailsManager(accounts);
    }
}
