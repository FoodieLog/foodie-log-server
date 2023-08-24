package com.foodielog.application._core.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.foodielog.application.feed.dto.FeedSaveDTO;
import com.foodielog.application.feed.dto.LikeFeedDTO;
import com.foodielog.application.feed.dto.ReportFeedDTO;
import com.foodielog.application.feed.dto.UpdateFeedDTO;
import com.foodielog.application.reply.dto.ReplyCreatDTO;
import com.foodielog.application.user.dto.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class ApplicationSwaggerConfig {

    private static final String SERVICE_NAME = "푸디로그";
    private static final String API_VERSION = "V1";
    private static final String API_DESCRIPTION = "내 손 안의 맛집 지도";
    private static final String API_URL = "15.165.93.123:8080";

    @Bean(name = "applicationApi")
    public Docket api(TypeResolver typeResolver) {
        Docket docket = new Docket(DocumentationType.OAS_30);

        setRequestDTO(docket, typeResolver);

        return docket.apiInfo(apiInfo())
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .ignoredParameterTypes(Errors.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.foodielog.application"))
                .paths(PathSelectors.any())
                .build();
    }

    private Docket setRequestDTO(Docket docket, TypeResolver typeResolver) {
        return docket
                .additionalModels(typeResolver.resolve(FeedSaveDTO.Request.class))
                .additionalModels(typeResolver.resolve(LikeFeedDTO.Request.class))
                .additionalModels(typeResolver.resolve(ReplyCreatDTO.Request.class))
                .additionalModels(typeResolver.resolve(ChangeNotificationDTO.Request.class))
                .additionalModels(typeResolver.resolve(ChangePasswordDTO.Request.class))
                .additionalModels(typeResolver.resolve(ChangeProfileDTO.Request.class))
                .additionalModels(typeResolver.resolve(LoginDTO.Request.class))
                .additionalModels(typeResolver.resolve(ReissueDTO.Request.class))
                .additionalModels(typeResolver.resolve(ResetPasswordDTO.Request.class))
                .additionalModels(typeResolver.resolve(SignUpDTO.Request.class))
                .additionalModels(typeResolver.resolve(WithdrawDTO.Request.class))
                .additionalModels(typeResolver.resolve(UpdateFeedDTO.Request.class))
                .additionalModels(typeResolver.resolve(ReportFeedDTO.Request.class));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(SERVICE_NAME)
                .description(API_DESCRIPTION)
                .version(API_VERSION)
                .termsOfServiceUrl(API_URL)
                .build();
    }
}
