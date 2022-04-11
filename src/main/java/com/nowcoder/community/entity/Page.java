package com.nowcoder.community.entity;

/**
 * 封装分页的组件
 */
public class Page {
    // 当前的页面
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 查询总数
    private int rows;
    // 返回查询路径，用于复用分页连接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前行的起始行
     *
     * @return
     */
    public int getOffset() {
        // current * limit -limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        return (rows % limit == 0) ? rows/limit : rows/limit + 1;
    }

    /**
     * 起始页码
     *
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * 终止页码
     *
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
