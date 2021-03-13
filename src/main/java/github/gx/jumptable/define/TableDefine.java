package github.gx.jumptable.define;

/**
 * @program: RedisGrowUp
 * @description: 跳表相关常量定义类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-03-11 16:15
 **/
public class TableDefine {

    /**
     * 层数每次增加的概率
     */
    public static double TARGET_P = 0.25;

    /**
     * 最大层数，对于 C 可以通过这种方式分配空间，使用 list 避免了该问题
     */
    public static int MAX_FLOOR = 64;
}
