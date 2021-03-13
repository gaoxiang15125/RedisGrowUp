package github.gx.jumptable;

import github.gx.jumptable.define.TableDefine;
import github.gx.jumptable.dto.JumpFloorNode;
import github.gx.jumptable.dto.JumpNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: RedisGrowUp
 * @description: 跳表实例对象，具体逻辑实现
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-03-11 16:07
 **/
public class JumpTable {

    private static Random random = new Random();

    /**
     * 头尾节点，头部指向 空节点，方便处理链表为空的情况
     */
    private JumpNode headNode;
    private JumpNode tailNode;

    /**
     * 链表内有意义的节点个数
     */
    private int nodeNum;

    /**
     * 链表最大层级 即当前链表节点中最大层层数
     */
    private int floor;

    /**
     * 初始化 JumpTable 对象，创建空的头节点
     */
    public JumpTable() {
        headNode = new JumpNode();
        tailNode = headNode;
        floor = 1;
    }

    // 对于跳表，只需要实现 增删查的逻辑即可

    /**
     * 根据目标分数查找对应节点，包含分数相同情况
     * 因为参考了 redis 的实现，理论上还应提供 根据排名查询，逻辑差不多不再赘述
     * @param score
     * @return 分数与目标分数相同的 节点集合, 不存在返回 null
     */
    public List<JumpNode> findNodeByValue(double score) {
        // 按照 score 大小查找对应节点
        JumpNode targetNode = headNode;

        // 层级遍历，查找恰好比目标值小一点的节点
        for(int i=floor-1;i>=0;i--) {
            JumpNode buffNode = targetNode.getFloorNodes()[i].getForwardNode();
            while(buffNode != null && score > buffNode.getScore()) {
                targetNode = buffNode;
            }
            continue;
        }
        targetNode = tailNode.getFloorNodes()[0].getForwardNode();
        if(targetNode!=null && targetNode.getScore() == score) {
            List<JumpNode> resultList = new ArrayList<JumpNode>();
            while(targetNode!=null && targetNode.getScore() == score) {
                resultList.add(targetNode);
                targetNode = targetNode.getFloorNodes()[0].getForwardNode();
            }
            return resultList;
        } else {
            return null;
        }
    }

    public void insertNode(JumpNode insertNode) {
        // 通过遍历找到当前 Node 在 每一层的对应位置，依次进行修改
        // 构建 update、rank 数组，记录当前层需要修改的上个节点、前面的节点个数
        // 前者用来进行插入操作，后者用来指导按次序获取值
        int newLevel = getRandomFloorNum();
        JumpNode[] updateNode = new JumpNode[Math.max(newLevel, floor)];
        int[] ranks = new int[Math.max(newLevel,floor)];

        updateNode[floor-1] = headNode;
        for(int i=floor-1;i>=0;i--) {
            JumpNode buffNode = updateNode[floor-1].getFloorNodes()[i].getForwardNode();
            while (buffNode!=null && buffNode.getScore()<insertNode.getScore()) {
                updateNode[i] = buffNode;
                // 更新 ranks ，加上当前跨越量
                ranks[i] = ranks[i] += buffNode.getFloorNodes()[i].getSpanNum();
            }
            // 将当前层保留到下一层
            if(i>0) {
                updateNode[i-1] = updateNode[i];
                ranks[i-1] = ranks[i];
            }
        }
        // 填充多出来的层级
        for(int i=floor;i<newLevel;i++) {
            ranks[i] = 0;
            updateNode[i] = headNode;
            updateNode[i].getFloorNodes()[i].setSpanNum(nodeNum + 1);
        }

        // 按照 从下往上的顺序将节点加入指定位置
        for(int i=0;i<floor;i++) {
            JumpFloorNode jumpFloorNode = new JumpFloorNode();
            jumpFloorNode.setSpanNum(updateNode[i].getFloorNodes()[0].getSpanNum() - ranks[0] + ranks[i]);
            jumpFloorNode.setForwardNode(updateNode[i].getFloorNodes()[i].getForwardNode());
            insertNode.getFloorNodes()[i] = jumpFloorNode;

            setFloorInfo(updateNode[i].getFloorNodes(), insertNode, (ranks[0] - ranks[1] + 1), i);
        }

        // 收尾工作，修改 JumpTable 相关常量
        // 尾节点 跳表长度等
        if(updateNode[0] == headNode) {
            insertNode.setBackward(null);
        } else {
            insertNode.setBackward(updateNode[0]);
        }
        if(insertNode.getFloorNodes()[0].getForwardNode()!=null) {
            insertNode.getFloorNodes()[0].getForwardNode().setBackward(insertNode);
        } else {
            tailNode = insertNode;
        }
        nodeNum ++;
    }

    /**
     * 获取当前节点的层数，使用随机数获取其层数
     * Redis 设置的 增长概率为 0.25，但是 我们心里清楚 越接近 e 效率越高
     * 预期层数为： 1/(1-P)
     * @return
     */
    private static int getRandomFloorNum() {
        int floor = 1;
        double randomResult = random.nextDouble();
        while (randomResult< TableDefine.TARGET_P) {
            floor ++;
            randomResult = random.nextDouble();
        }
        return floor;
    }

    private void setFloorInfo(JumpFloorNode[] jumpFloorNodes, JumpNode node, int span, int floorNum) {
        JumpFloorNode floorNode = jumpFloorNodes[floorNum];
        if(floorNode == null) {
            floorNode = new JumpFloorNode();
            jumpFloorNodes[floorNum] = floorNode;
        }
        floorNode.setForwardNode(node);
        floorNode.setSpanNum(span);
    }
}
