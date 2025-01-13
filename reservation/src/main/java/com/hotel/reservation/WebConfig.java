package com.hotel.reservation;

import com.hotel.reservation.domain.room.RoomService;
import com.hotel.reservation.web.argumentresolver.LoginMemberArgumentResolver;
import com.hotel.reservation.web.interceptor.LoginCheckInterceptor;
import com.hotel.reservation.web.interceptor.LoginIdCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RoomService roomService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/join", "/login", "/logout", "/error", "/member/addMessage", "/room/search", "/room/searchMore", "/roominfo/images/**",
                        "/myPage/roominfo/images/**", "/css/**", "/*.ico", "/js/**", "/*.svg", "/assets/**");

        registry.addInterceptor(new LoginIdCheckInterceptor(roomService))
                .order(2)
                .addPathPatterns("/room/update/**", "/room/delete/**", "/room/view/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }



}
