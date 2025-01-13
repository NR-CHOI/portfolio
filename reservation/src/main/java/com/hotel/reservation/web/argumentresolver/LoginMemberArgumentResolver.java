package com.hotel.reservation.web.argumentresolver;

import com.hotel.reservation.domain.member.findMemberDto;
import com.hotel.reservation.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //@Login 애노테이션이 있으면서, findMemberDto 타입이면 ArgumentResolver가 사용됨.
        log.info("supportsParameter 실행");
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = findMemberDto.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        //세션에 있는 로그인회원정보인 findMemberDto 객체를 찾아서 반환, 컨트롤러의 메서드 파라미터에 findMemberDto 객체 전달
        log.info("resolverArgument 실행");
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        findMemberDto findMember= null;
            try{
                findMember = (findMemberDto) session.getAttribute(SessionConst.LOGIN_MEMBER);
                return findMember;
            }catch (NullPointerException e){
                //세션에 로그인 데이터 없을 경우
                log.info("세션에 로그인 데이터 없음: findMember={}", findMember);
                return findMember;
            }
    }
}
