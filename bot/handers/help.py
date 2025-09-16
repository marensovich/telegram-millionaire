from aiogram import Router, types
from aiogram.filters import Command

router = Router()

@router.message(Command("help"))
async def cmd_help(message: types.Message):
    await message.answer("Доступные команды:\n/start - запуск\n/help - помощь\n/quiz - начать квиз")
