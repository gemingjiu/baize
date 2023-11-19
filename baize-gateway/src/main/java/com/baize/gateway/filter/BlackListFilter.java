package com.baize.gateway.filter;

import com.baize.common.core.utils.ServletUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 黑名单过滤器
 *
 * @author gemj
 */
@Component
public class BlackListFilter extends AbstractGatewayFilterFactory<BlackListFilter.Config> {
    public BlackListFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String url = exchange.getRequest().getURI().getPath();
            if (config.matchBlacklist(url)) {
                return ServletUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private List<String> blackList;

        private List<Pattern> blackListPattern = new ArrayList<>();

        public boolean matchBlacklist(String url) {
            return !blackListPattern.isEmpty() && blackListPattern.stream().anyMatch(p -> p.matcher(url).find());
        }

        public List<String> getBlackList() {
            return blackList;
        }

        public void setBlackList(List<String> blackList) {
            this.blackList = blackList;
            this.blackListPattern.clear();
            this.blackList.forEach(url -> {
                this.blackListPattern.add(Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
            });
        }
    }

}