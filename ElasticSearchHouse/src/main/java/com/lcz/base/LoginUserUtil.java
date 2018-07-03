package com.lcz.base;

import java.util.regex.Pattern;

import org.springframework.security.core.context.SecurityContextHolder;

import com.lcz.entity.User;

/**
 * Created by codingchaozhang.
 */
public class LoginUserUtil {
    private static final String PHONE_REGEX = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if (user == null) {
            return -1L;
        }

        return user.getId();
    }

    /**
     * 验证手机号码
     *
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param target 目标号码
     * @return 如果是手机号码 返回true; 反之,返回false
     */
    public static boolean checkTelephone(String target) {
        return PHONE_PATTERN.matcher(target).matches();
    }

    /**
     * 验证一般的英文邮箱
     * @param target 目标邮箱
     * @return 如果符合邮箱规则 返回true; 反之,返回false
     */
    public static boolean checkEmail(String target) {
        return EMAIL_PATTERN.matcher(target).matches();
    }
}
