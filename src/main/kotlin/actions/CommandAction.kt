package com.yumi.kotlin.actions

import com.yumi.kotlin.util.contains
import com.yumi.kotlin.util.isManager
import com.yumi.kotlin.util.startsWith
import com.yumi.kotlin.util.yes
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.At

object CommandAction : MessageHandler {

    override suspend fun invoke(event: GroupMessageEvent) {
        event.apply {
            startsWith("/recall").and(contains("on")).and(isManager()).yes {
                reply(RecallAction.setAntiRecallConfig(this.group.id, true))
            }
            startsWith("/recall").and(contains("off")).and(isManager()).yes {
                reply(RecallAction.setAntiRecallConfig(this.group.id, false))
            }
            startsWith("/recall").and(contains("add")).and(isManager()).yes {
                this.message[At]?.let {
                    quoteReply(RecallAction.addMember(it.asMember()))
                }
            }
            startsWith("/recall").and(contains("remove")).and(isManager()).yes {
                this.message[At]?.let {
                    quoteReply(RecallAction.removeMember(it.asMember()))
                }
            }
            startsWith("/recall").and(contains("status")).yes {
                RecallAction.showMembers(this.group.id,this.sender)?.let {
                    reply(it)
                }
            }
            startsWith("/女装大佬").yes {
                BoysAction().invoke(this)
            }
        }
    }
}