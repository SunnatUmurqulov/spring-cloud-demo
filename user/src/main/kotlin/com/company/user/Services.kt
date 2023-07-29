package com.company.user

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.transaction.Transactional

@FeignClient(name = "subscription")
interface SubscriptionService {
    @PostMapping("/internal/create")
    fun createSubscription(@RequestBody subscriptionDto: SubscriptionDto): Boolean
}


interface UserService {
    fun create(dto: UserDto): Any
    fun update(userId: Long, dto: UserDto): String
    fun getOne(id: Long): UserDto
    fun getAll(pageable: Pageable): Page<UserDto>
    fun delete(id: Long)
    fun confirmUserAccount(emailCode: String, email: String): String
    fun existById(id: Long): Boolean
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val subscriptionService: SubscriptionService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val javaMailSender: JavaMailSender,
) : UserService {
    @Transactional
    override fun create(dto: UserDto): Any {
        if (userRepository.existsByUsername(dto.username)) {
            throw UsernameExistsException(dto.username)
        }

        val emailValid = isEmailValid(dto.email)
        if (!emailValid) throw EmailValidException(dto.email)


        if (userRepository.existsByEmail(dto.email)) {
            throw UserEmailExistsException(dto.email)
        }
        val verificationCode = UUID.randomUUID().toString()
        dto.run {
            val user = User(
                firstName,
                lastName,
                username,
                email,
                passwordEncoder.encode(password),
                false,
                verificationCode
            )
            userRepository.save(user)
            val subscriptionDto = SubscriptionDto(user.id!!)
            subscriptionService.createSubscription(subscriptionDto)
            val sendEmail = sendEmail(user.email, user.emailCode)
            if (sendEmail) return ApiResponse("Success", true)
        }
        return ApiResponse("Error", false)
    }


    override fun update(userId: Long, dto: UserDto): String {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException(userId)
        dto.run {
            user.firstName = firstName
            user.lastName = lastName
            user.password = passwordEncoder.encode(password)
            user.username = username
        }
        userRepository.save(user)
        return "Success"
    }

    override fun getOne(id: Long): UserDto {
        return userRepository.findByIdAndDeletedFalse(id)?.let { UserDto.toDto(it) }
            ?: throw UserNotFoundException(id)
    }

    override fun getAll(pageable: Pageable): Page<UserDto> {
        return userRepository.findAllNotDeleted(pageable).map { UserDto.toDto(it) }
    }

    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundException(id)
    }

    override fun confirmUserAccount(emailCode: String, email: String): String {
        val user = userRepository.findByEmailAndEmailCode(emailCode, email) ?: throw UserEmailNotFoundException(
            emailCode,
            email
        )
        user.emailCode = null.toString()
        user.active = true
        userRepository.save(user)
        return "Success"
    }

    override fun existById(id: Long): Boolean {
        return userRepository.existsById(id)
    }

    fun sendEmail(sendingEmail: String, emailCode: String): Boolean {
        return try {
            val mailMessage = SimpleMailMessage()
            mailMessage.setFrom("Threads@gmail.com")
            mailMessage.setTo(sendingEmail)
            mailMessage.setSubject("Akkauntni tasdiqlash")
            mailMessage.setText("<a href='http://localhost:8080/api/v1/user/auth/verifyEmail?$emailCode&email=$sendingEmail'>Tasdiqlang</a>")
            javaMailSender.send(mailMessage)
            true
        } catch (exception: Exception) {
            false
        }
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@gmail.com\$"
        return email.matches(emailRegex.toRegex())
    }


}