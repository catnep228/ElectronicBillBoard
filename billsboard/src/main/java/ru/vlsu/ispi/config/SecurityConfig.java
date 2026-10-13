package ru.vlsu.ispi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            .authorizeHttpRequests(auth -> auth

                /*
                 * =========================
                 * ПУБЛИЧНЫЕ РЕСУРСЫ
                 * =========================
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/auth/login"),
                    new AntPathRequestMatcher("/auth/register")
                ).permitAll()


                /*
                 * CSS / JS / КАРТИНКИ / ФОТО
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/uploads/**")
                ).permitAll()


                /*
                 * =========================
                 * ГЛАВНАЯ СТРАНИЦА
                 * =========================
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/announcements"),
                    new AntPathRequestMatcher("/news"),
                    new AntPathRequestMatcher("/about")
                ).permitAll()


                /*
                 * =========================
                 * ПРОСМОТР ОБЪЯВЛЕНИЙ
                 * =========================
                 *
                 * Сами объявления можно
                 * смотреть без авторизации.
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/ads/*")
                ).permitAll()


                /*
                 * =========================
                 * ОТПРАВКА СООБЩЕНИЯ
                 * =========================
                 *
                 * Отправлять сообщение
                 * может только авторизованный.
                 *
                 * Это правило должно находиться
                 * ДО общего /ads/**.
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/ads/*/message")
                ).authenticated()


                /*
                 * =========================
                 * РЕДАКТИРОВАНИЕ ОБЪЯВЛЕНИЙ
                 * =========================
                 *
                 * Сам факт авторизации ещё
                 * не означает владение объявлением.
                 *
                 * Проверка владельца находится
                 * в AdController.
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/ads/*/edit")
                ).authenticated()


                /*
                 * =========================
                 * АДМИНИСТРАТОР
                 * =========================
                 *
                 * Только ROLE_ADMIN.
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/admin/**")
                ).hasRole("ADMIN")


                /*
                 * =========================
                 * СОЗДАНИЕ ОБЪЯВЛЕНИЯ
                 * =========================
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/FormForAd")
                ).authenticated()


                /*
                 * =========================
                 * ПРОФИЛЬ
                 * =========================
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/profile"),
                    new AntPathRequestMatcher("/YOU")
                ).authenticated()


                /*
                 * =========================
                 * ЧАТ
                 * =========================
                 */

                .requestMatchers(
                    new AntPathRequestMatcher("/chat/**")
                ).authenticated()


                /*
                 * =========================
                 * ВСЁ ОСТАЛЬНОЕ
                 * =========================
                 */

                .anyRequest().authenticated()
            )


            /*
             * =========================
             * LOGIN
             * =========================
             */

            .formLogin(form -> form

                .loginPage("/auth/login")

                .loginProcessingUrl("/auth/login")

                .defaultSuccessUrl("/", true)

                .permitAll()
            )


            /*
             * =========================
             * LOGOUT
             * =========================
             */

            .logout(logout -> logout

                .logoutUrl("/auth/logout")

                .logoutSuccessUrl("/auth/login?logout")

                .permitAll()
            );


        return http.build();
    }
}