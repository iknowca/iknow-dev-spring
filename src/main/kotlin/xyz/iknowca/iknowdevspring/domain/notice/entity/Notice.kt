package xyz.iknowca.iknowdevspring.domain.notice.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Notice(
    val title:String,
    val content:String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long?=null
) {
}