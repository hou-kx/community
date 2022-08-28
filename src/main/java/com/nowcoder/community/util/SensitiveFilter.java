package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换字符串
    private static final String REPLACEMENT = "***";
    private final TrieNode root = new TrieNode();

    /**
     * 当前工具类由 spring 容器管理
     * - @PostConstruct 方法注解，表示这个方法在构造方法之后调用
     * - @PreDestroy //在bean 销毁之前调用
     *
     * - this.getClass()
     * - this.getClass().getClassLoader() 获得类加载器
     * - this.getClass().getClassLoader().getResourceAsStream(name:?) 这里是获取 class 的加载路径下文件的文件流
     */
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    /**
     * 添加敏感词到前缀树中
     *
     * @param keyword 敏感词
     */
    private void addKeyword(String keyword) {
        TrieNode curNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            // 获取 敏感此节点
            TrieNode subNode = curNode.getSubNode(c);
            if (subNode == null) {
                // 初始化当前敏感词子树节点
                subNode = new TrieNode();
                curNode.addSubNode(c, subNode);
            }
            // 当前节点指针维护，向下继续生成前缀树
            curNode = subNode;
            // 来的结尾设置叶子节点标识
            if (i == keyword.length() - 1) {
                curNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待敏感词过滤字符串
     * @return 过滤后的字符串
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        TrieNode curNode = root;
        // 敏感词位置
        int begin = 0, position = 0;
        // result
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            // 1. 跳过符号  *装*1*3*
            if (isSymbol(c)) {
                // 若第一个字符是符号那就直接计入记过中
                if (curNode == root) {
                    sb.append(c);
                    begin++;
                }
                position++; // 无论符号在哪 position 指针都指向下个
                continue;
            }

            // 2. 这里就不是符号了，则需要进行过滤操作了，检查下级节点：
            curNode = curNode.getSubNode(c);
            if (curNode == null) {  // (1) 没有下级节点，以 begin 开头没有敏感词，2-
                sb.append(text.charAt(begin));
                // 当前不是敏感此则，begin 后移，重新指向根节点
                position = ++begin;
                curNode = root;
            } else if (curNode.isKeywordEnd()) {    // (2) 当前已经来到了，叶子结点，也就是 begin-position 之前的字符串为敏感字符串
                // 发现敏感词进行替换，然后进入下个位置
                sb.append(REPLACEMENT);
                begin = ++position;
                curNode = root;
            } else {    // (3) 当前正在核验，直接核验下个字符
                position++;
            }
        }
        // 3. 最后一批字符直接追加结果字符串后面
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 若是符号返回 true
    private boolean isSymbol(Character c) {
        // 0x2e80 ~ 0x9fff 表示东亚文字符号，日文、韩文、中文之类的
        // isAsciiAlphanumeric 包含 大小写字母、数字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0X9FFF);
    }

    /**
     * 定义一个内部类，实现前缀树的数据结构
     */
    private class TrieNode {
        // 关键词结束标识, 即叶子节点
        private boolean isKeywordEnd = false;

        // 子节点 key-是下级节点的字符，value-是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 添加、获取子节点
         *
         * @param c    key-是下级节点的字符
         * @param node value-是下级节点
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

    }
}
