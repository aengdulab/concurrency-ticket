package com.aengdulab.ticket.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aengdulab.ticket.domain.Member;
import com.aengdulab.ticket.domain.MemberTicket;
import com.aengdulab.ticket.domain.Ticket;
import com.aengdulab.ticket.repository.MemberRepository;
import com.aengdulab.ticket.repository.MemberTicketRepository;
import com.aengdulab.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@SuppressWarnings("NonAsciiCharacters")
class MemberTicketServiceTest {

    @Autowired
    private MemberTicketService memberTicketService;

    @Autowired
    private MemberTicketRepository memberTicketRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        memberTicketRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
    }

    @Test
    void 멤버_티켓_발행_제한_수를_초과하면_예외가_발생한다() {
        Member member = memberRepository.save(new Member("행성이"));
        Ticket ticket = ticketRepository.save(new Ticket("목성행", 5L));
        memberTicketRepository.save(new MemberTicket(member, ticket));
        memberTicketRepository.save(new MemberTicket(member, ticket));

        assertThatThrownBy(() -> memberTicketService.issue(member.getId(), ticket.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("계정당 구매할 수 있는 티켓 수량을 넘었습니다.");
    }

    @Test
    void 티켓_발행_제한_수를_초과하면_예외가_발생한다() {
        Member member = memberRepository.save(new Member("행성이"));
        Ticket ticket = ticketRepository.save(new Ticket("목성행", 0L));

        assertThatThrownBy(() -> memberTicketService.issue(member.getId(), ticket.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("티켓 재고가 소진되었습니다.");
    }
}
