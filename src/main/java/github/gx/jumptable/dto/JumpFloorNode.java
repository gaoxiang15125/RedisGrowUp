package github.gx.jumptable.dto;

import lombok.Data;

/**
 * @program: RedisGrowUp
 * @description: 跳表每层后置节点维护对象
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-03-11 16:01
 **/
@Data
public class JumpFloorNode {

    /**
     * 下一个节点引用地址
     */
    private JumpNode forwardNode;

    /**
     * 当前节点 与 下个节点之间的节点个数，记录跳过了多少个节点
     */
    private int spanNum;
}
