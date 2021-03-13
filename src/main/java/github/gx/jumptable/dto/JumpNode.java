package github.gx.jumptable.dto;

import github.gx.jumptable.define.TableDefine;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RedisGrowUp
 * @description: 跳表子节点 定义类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-03-11 15:52
 **/
@Data
public class JumpNode {

    /**
     * 排列表 描述项
     */
    private String info;

    /**
     * 排列表分值
     */
    private double score;

    /**
     * 反向遍历链表索引
     */
    private JumpNode backward;

    /**
     * 存储每层的节点 后置节点信息,不应该使用链表，链表失去了价值
     */
    private JumpFloorNode[] floorNodes;

    /**
     * 当前节点包含几层
     */
    private int floorNum;

    public JumpNode() {
        floorNodes = new JumpFloorNode[TableDefine.MAX_FLOOR];
    }
}
