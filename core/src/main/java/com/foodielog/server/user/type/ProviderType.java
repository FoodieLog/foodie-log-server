package com.foodielog.server.user.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProviderType {
	ME("자체가입"),
	KAKAO("카카오");

	private final String label;

	//    public static ProviderType findByLabel(String label){
	//        return Arrays.stream(ProviderType.values())
	//                .filter(o->o.getLabel().equals(label))
	//                .findAny()
	//                .orElseThrow(Exception400::new);
	//    }
}
