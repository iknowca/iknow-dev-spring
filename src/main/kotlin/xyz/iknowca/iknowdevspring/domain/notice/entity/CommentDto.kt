package xyz.iknowca.iknowdevspring.domain.notice.entity

import xyz.iknowca.iknowdevspring.domain.account.service.form.AccountDto

class CommentDto(
    val id: Long?,
    val writer: AccountDto?,
    val content: String
) {
}